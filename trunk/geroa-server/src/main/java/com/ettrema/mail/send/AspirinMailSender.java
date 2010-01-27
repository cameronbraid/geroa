package com.ettrema.mail.send;

import com.ettrema.mail.StandardMessage;
import com.ettrema.mail.StandardMessageFactory;
import com.ettrema.mail.StandardMessageFactoryImpl;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.mailet.MailAddress;
import org.masukomi.aspirin.core.MailQue;
import org.masukomi.aspirin.core.MailWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspirinMailSender implements MailSender, MailWatcher {

    private final static Logger log = LoggerFactory.getLogger(AspirinMailSender.class);
    private static AspirinMailSender theInstance;

    public static AspirinMailSender createInstance(int retryInterval, int deliveryThreads, String postmaster, int maxRetries) {
        if (theInstance != null) {
            throw new RuntimeException("Instance already created");
        }
        return new AspirinMailSender(retryInterval, deliveryThreads, postmaster, maxRetries);
    }

    private boolean started;

    public AspirinMailSender() {
        this(1000, 2, "admin@localhost", 3);
    }



    /**
     * 
     * @param retryInterval - eg 1000
     * @param deliveryThreads - eg 2
     * @param postmaster - eg admin@ettrema.com
     * @param maxRetries - eg 3
     */
    public AspirinMailSender(int retryInterval, int deliveryThreads, String postmaster, int maxRetries) {
        System.setProperty("aspirinRetryInterval", retryInterval + "");
        System.setProperty("aspirinDeliverThreads", deliveryThreads + "");
        System.setProperty("aspirinPostmaster", postmaster);
        System.setProperty("aspirinMaxAttempts", maxRetries + "");
    }

    public void sendMail(MimeMessage mm) {
        if( !started ) {
            throw new RuntimeException("This mail sender is stopped");
        }
        try {
            MailQue.queMail(mm);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deliverySuccess(MimeMessage message, Collection recipients) {
        log.info("deliverySuccess: writing content");
    }

    public void deliveryFailure(MimeMessage message, Collection recipients) {
        log.info("deliveryFailure");
    }

    public void deliverySuccess(MimeMessage message, MailAddress recipient) {
        log.info("deliverySuccess");
    }

    public void deliveryFailure(MimeMessage message, MailAddress recipient) {
        log.info("deliveryFailure");
    }

    public void sendMail(String from, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
        if( !started ) {
            throw new RuntimeException("This mail sender is stopped");
        }
        try {
            MimeMessage mm = new MimeMessage(getSession());
            mm.setSubject(subject);
            mm.setFrom(new InternetAddress(from, fromPersonal));
            Address[] add = new Address[1];
            add[0] = new InternetAddress(replyTo);
            mm.setReplyTo(add);
            for (String sTo : to) {
                mm.addRecipient(RecipientType.TO, new InternetAddress(sTo));
            }
            mm.setContent(text, "text/plain");
            sendMail(mm);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException messagingException) {
            throw new RuntimeException(messagingException);
        }
    }

    public Session getSession() {
        Properties props = new Properties();        
        return Session.getInstance(props);
    }
    
    public MimeMessage newMessage(MimeMessage mm) {
        try {
            return new MySmtpMessage(getSession(), mm);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public MimeMessage newMessage() {
        return new MimeMessage(getSession());
    }

    public void start() {
        this.started = true;
        MailQue.addWatcher(this);
    }

    public void stop() {
        this.started = false;
        MailQue.removeWatcher(this);
    }

    public void sendMail( StandardMessage sm ) {
        StandardMessageFactory smf = new StandardMessageFactoryImpl();
        MimeMessage mm  = newMessage();
        smf.toMimeMessage( sm, mm );
        sendMail( mm );
    }
}
