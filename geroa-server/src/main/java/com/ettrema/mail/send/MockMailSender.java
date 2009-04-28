package com.ettrema.mail.send;

import com.ettrema.mail.StandardMessage;
import com.ettrema.mail.StandardMessageFactory;
import com.ettrema.mail.StandardMessageFactoryImpl;
import com.sun.mail.smtp.SMTPMessage;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 */
public class MockMailSender implements MailSender{

    private boolean isStarted;
    private final List<SentMessage> sentMessages  = new ArrayList<SentMessage>();
    private final List<MimeMessage> sentMimeMessages = new ArrayList<MimeMessage>();

    public MockMailSender() {
    }


    public void start() {
        this.isStarted = true;
    }

    public void stop() {
        this.isStarted = false;
    }


    public void sendMail(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
        SentMessage msg = new SentMessage(fromAddress, fromPersonal, to, replyTo, subject, text);
        sentMessages.add(msg);
    }

    public Session getSession() {
        return null;
    }

    public void sendMail(MimeMessage mm) {
        sentMimeMessages.add(mm);
    }

    public void sendMail( StandardMessage sm ) {
        StandardMessageFactory smf = new StandardMessageFactoryImpl();
        MimeMessage mm  = newMessage();
        smf.toMimeMessage( sm, mm );
        sendMail( mm );
    }



    public MimeMessage newMessage(MimeMessage mm) {
        try {
            return new MySmtpMessage(getSession(), mm);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public MimeMessage newMessage() {
        return new SMTPMessage(getSession());
    }

    public List<SentMessage> getSentMessages() {
        return sentMessages;
    }

    public List<MimeMessage> getSentMimeMessages() {
        return sentMimeMessages;
    }

    

    public class SentMessage {
        public final String fromAddress;
        public final String fromPersonal;
        public final List<String> to;
        public final String replyTo;
        public final String subject;
        public final String text;

        public SentMessage(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
            this.fromAddress = fromAddress;
            this.fromPersonal = fromPersonal;
            this.to = to;
            this.replyTo = replyTo;
            this.subject = subject;
            this.text = text;
        }
    }

}
