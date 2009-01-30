package com.ettrema.mail.mock;

import com.ettrema.mail.send.MailSender;
import java.util.List;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MockMailSender implements MailSender {

    public void start() {
    }

    public void stop() {
    }

    public void sendMail(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
    }

    public Session getSession() {
        return null;
    }

    public void sendMail(MimeMessage mm) {
    }

    public MimeMessage newMessage(MimeMessage mm) {
        return null;
    }

    public MimeMessage newMessage() {
        return null;
    }
}
