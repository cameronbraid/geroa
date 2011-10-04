package com.ettrema.mail.send;

import com.ettrema.mail.StandardMessage;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class RetryingMailSender implements MailSender {

	private final static Logger log = LoggerFactory.getLogger(RetryingMailSender.class);
	
	private final MailSender mailSender;
	private DelayQueue<DelayMessage> delayQueue = new DelayQueue<DelayMessage>();

	private boolean running;
	
	private Consumer consumer;
	
	private Thread thConsumer;
	
	private int maxRetries = 3;
	
	public RetryingMailSender(MailSender mailSender) {
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
	
	

	public void sendMail(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
		DelayMessage dm = new DelayMessage(fromAddress, fromPersonal, to, replyTo, subject, text);
		delayQueue.add(dm);
		log.info("Queue size is now: " + delayQueue.size());
	}

	public void sendMail(MimeMessage mm) {
		DelayMessage dm = new DelayMessage(mm);
		delayQueue.add(dm);
		log.info("Queue size is now: " + delayQueue.size());
	}

	public void sendMail(StandardMessage sm) {
		DelayMessage dm = new DelayMessage(sm);
		delayQueue.add(dm);
		log.info("Queue size is now: " + delayQueue.size());
	}

	public MimeMessage newMessage(MimeMessage mm) {
		return mailSender.newMessage(mm);
	}

	public MimeMessage newMessage() {
		return mailSender.newMessage();
	}

	public Session getSession() {
		return mailSender.getSession();
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
			} catch(Throwable e) {
				dm.onFailed();
				if( dm.attempts <= maxRetries ) {
					log.info("Failed to send message: " + dm + " will retry in " + dm.getDelayMillis()/1000 + "seconds");
					queue.add(dm);
				} else {
					log.error("Failed to send message: " + dm + " Exceeded retry attempts: " + dm.attempts + ", will not retry");
				}
			}
		}

		private void send(DelayMessage dm) {
			if( dm.mm != null ) {
				sendMail(dm.mm);
			} else if( dm.sm != null ) {
				sendMail(dm.sm);
			} else {
				sendMail(dm.fromAddress, dm.fromPersonal, dm.to, dm.replyTo, dm.subject, dm.text);
			}
		}
	}

	private class DelayMessage implements Delayed {

		private final MimeMessage mm;
		private final StandardMessage sm;
		private String fromAddress;
		private final String fromPersonal;
		private final List<String> to;
		private final String replyTo;
		private final String subject;
		private final String text;
		private int attempts;
		

		public DelayMessage(MimeMessage mm) {
			this.mm = mm;
			this.sm = null;
			this.fromAddress = null;
			this.fromPersonal = null;
			this.to = null;
			this.replyTo = null;
			this.subject = null;
			this.text = null;
		}

		public DelayMessage(StandardMessage sm) {
			this.sm = sm;
			this.mm = null;
			this.fromAddress = null;
			this.fromPersonal = null;
			this.to = null;
			this.replyTo = null;
			this.subject = null;
			this.text = null;
		}

		public DelayMessage(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
			this.fromAddress = fromAddress;
			this.fromPersonal = fromPersonal;
			this.to = to;
			this.replyTo = replyTo;
			this.subject = subject;
			this.text = text;
			this.sm = null;
			this.mm = null;
		}

		public String getFromAddress() {
			return fromAddress;
		}

		public String getFromPersonal() {
			return fromPersonal;
		}

		public MimeMessage getMm() {
			return mm;
		}

		public String getReplyTo() {
			return replyTo;
		}

		public StandardMessage getSm() {
			return sm;
		}

		public String getSubject() {
			return subject;
		}

		public String getText() {
			return text;
		}

		public List<String> getTo() {
			return to;
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

		private void onFailed() {
			attempts++;
		}
		
		public Long getDelayMillis() {
			if( attempts < 1 ) {
				return 0l; // no delay
			} else if( attempts <2) {
				return 5 * 1000l; // 5 seconds
			} else if( attempts < 3) {
				return 30 * 1000l; // 30 seconds
			} else {
				return attempts * 1000 * 60 * 60l; // attempts x hours Eg 5 attempts means a delay of 5 hours
			}
		}
	}
}
