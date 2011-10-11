package com.ettrema.mail.send;

import com.ettrema.mail.StandardMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintains a queue of emails to send, retrying a number of times if errors
 * occur.
 * 
 * Can optionally callback when an email is sent or has failed
 * 
 * Note that this class is intended to provide feedback about whether emails
 * were delivered or not, so it is not appropriate to use it with a MailSender
 * which buffers messages for sending.
 *
 * @author brad
 */
public class RetryingMailService {

	private final static Logger log = LoggerFactory.getLogger(RetryingMailService.class);
	private final MailSender mailSender;
	private Map<UUID,SendJob> mapOfJobs = new ConcurrentHashMap<UUID, SendJob>();
	private DelayQueue<DelayMessage> delayQueue = new DelayQueue<DelayMessage>();
	private boolean running;
	private Consumer consumer;
	private Thread thConsumer;
	private int maxRetries = 3;

	public RetryingMailService(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void start() {
		running = true;
		mailSender.start();
		consumer = new Consumer(delayQueue);
		thConsumer = new Thread(consumer);
		thConsumer.start();

	}

	public void stop() {
		running = false;
		delayQueue.clear();
		thConsumer.interrupt();
		mailSender.stop();
		consumer = null;
		thConsumer = null;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void sendMail(StandardMessage sm, EmailResultCallback callback) {
		DelayMessage dm = new DelayMessage(null, sm, callback);
		delayQueue.add(dm);
		log.info("Queue size is now: " + delayQueue.size());
	}

	/**
	 * Submit the list of emails to send and return a UUID identifying this job
	 * 
	 * @param sm
	 * @param callback
	 * @return 
	 */
	public UUID sendMails(List<StandardMessage> msgs, EmailResultCallback callback) {
		List<DelayMessage> list = new ArrayList<DelayMessage>();
		SendJob sendJob = new SendJob();
		for (StandardMessage sm  : msgs) {
			DelayMessage dm = new DelayMessage(sendJob, sm, callback);
			list.add(dm);
		}
		
		mapOfJobs.put(sendJob.id, sendJob);
		
		for(DelayMessage dm : sendJob.msgs) {
			delayQueue.add(dm);
		}
		log.info("Queue size is now: " + delayQueue.size());
		return sendJob.id;
	}
	
	public SendJob getJob(UUID id) {
		return mapOfJobs.get(id);
	}

	private class Consumer implements Runnable {

		private final DelayQueue<DelayMessage> queue;

		private Consumer(DelayQueue<DelayMessage> q) {
			queue = q;
		}

		public void run() {
			try {
				log.info("Starting queue processing consumer");
				while (running) {
					consume(queue.take());
					log.info("Remaining queue items: " + queue.size());
				}
			} catch (InterruptedException ex) {
				log.info("Exitting consumer thread");
			}
		}

		void consume(DelayMessage dm) {
			log.info("Attempt to send: " + dm);
			try {
				send(dm);
				dm.completedOk = true;
				dm.callback.onSuccess(dm.sm);
			} catch (Throwable e) {
				dm.onFailed(e);
				if (dm.attempts <= maxRetries) {
					log.info("Failed to send message: " + dm + " will retry in " + dm.getDelayMillis() / 1000 + "seconds");
					dm.failed = true;
					queue.add(dm);
				} else {
					log.error("Failed to send message: " + dm + " Exceeded retry attempts: " + dm.attempts + ", will not retry");
					dm.callback.onFailed(dm.sm, dm.lastException);
				}
			} finally {
				// If there is a sendJob, and all emails are sent, then call the finished callback
				if( dm.sendJob != null ) {
					if( dm.sendJob.checkComplete() ) {						
						dm.callback.finished(dm.sendJob.id, dm.sendJob.msgs);
					}
				}
			}
		}

		private void send(DelayMessage dm) {
			mailSender.sendMail(dm.sm);
		}
	}

	public class SendJob {

		private Collection<DelayMessage> msgs;
		private final UUID id;

		public SendJob() {
			this.id = UUID.randomUUID();
		}

		private boolean checkComplete() {
			for( DelayMessage dm : msgs) {
				boolean isComplete = dm.completedOk || dm.failed;
				if( !isComplete ) {
					return false;
				}
			}
			return true;
		}
	}

	public class DelayMessage implements Delayed {
		private final SendJob sendJob;
		private final StandardMessage sm;
		private final EmailResultCallback callback;
		private int attempts;
		private boolean completedOk;
		private boolean failed;
		private Throwable lastException;

		public DelayMessage(SendJob sendJob, StandardMessage sm, EmailResultCallback callback) {
			this.sendJob = sendJob;
			this.sm = sm;
			this.callback = callback;
		}

		public StandardMessage getSm() {
			return sm;
		}

		public long getDelay(TimeUnit unit) {
			return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
		}

		public int compareTo(Delayed o) {
			if (o instanceof DelayMessage) {
				DelayMessage other = (DelayMessage) o;
				return this.getDelayMillis().compareTo(other.getDelayMillis());
			} else {
				throw new RuntimeException("Not supported comparison with type: " + o.getClass() + " - should be: " + this.getClass());
			}
		}

		private void onFailed(Throwable e) {
			attempts++;
			this.lastException = e;
		}

		public Long getDelayMillis() {
			if (attempts < 1) {
				return 0l; // no delay
			} else if (attempts < 2) {
				return 5 * 1000l; // 5 seconds
			} else if (attempts < 3) {
				return 30 * 1000l; // 30 seconds
			} else {
				return attempts * 1000 * 60 * 60l; // attempts x hours Eg 5 attempts means a delay of 5 hours
			}
		}

		public boolean isCompletedOk() {
			return completedOk;
		}

		/**
		 * True if we've given up
		 * 
		 * @return 
		 */
		public boolean isFatal() {
			return failed;
		}

		public int getAttempts() {
			return attempts;
		}

		public Throwable getLastException() {
			return lastException;
		}
		
		
	}

	public interface EmailResultCallback {

		void onSuccess(StandardMessage sm);

		void onFailed(StandardMessage sm, Throwable lastException);

		void finished(UUID id, Collection<DelayMessage> msgs);
	}
}
