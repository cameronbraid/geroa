package com.ettrema.mail.send;

import java.util.List;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author brad
 */
public interface MailSender {
    
    public void start();

    public void stop();
    
    public void sendMail(String fromAddress, String fromPersonal, List<String> to, String replyTo, String subject, String text);
    
    public Session getSession();
    
    /**
     * Sends a message, assuming it was constructed using this MailSender's getSession
     * 
     * @param mm
     */
    public void sendMail(MimeMessage mm);
    
    public MimeMessage newMessage(MimeMessage mm);
    
    public MimeMessage newMessage();
}
