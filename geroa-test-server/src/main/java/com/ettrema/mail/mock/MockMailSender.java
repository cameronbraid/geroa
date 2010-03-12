package com.ettrema.mail.mock;

import com.ettrema.mail.StandardMessage;
import com.ettrema.mail.send.MailSender;
import java.util.List;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MockMailSender implements MailSender {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void sendMail(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text) {
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public void sendMail(MimeMessage mm) {
    }

    @Override
    public MimeMessage newMessage(MimeMessage mm) {
        return null;
    }

    @Override
    public MimeMessage newMessage() {
        return null;
    }

    @Override
    public void sendMail( StandardMessage sm ) {

    }
}
