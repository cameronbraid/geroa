package com.ettrema.mail;

import java.util.List;
import java.util.Map;

/**
 *  Interface which describes a standard message. This is a representation of
 *  an email which can have text, html and attachments. Use the StandardMessageFactory
 *  to populate these from a MimeMessage and to convert back again
 */
public interface StandardMessage {

    public List<StandardMessage> getAttachedMessages();

    public void setAttachedMessages(List<StandardMessage> attachedMessages);
   
    public String getSubject();
    
    public MailboxAddress getFrom();

    public List<Attachment> getAttachments();

    public void setFrom(MailboxAddress from);

    public MailboxAddress getReplyTo();

    public void setReplyTo(MailboxAddress replyTo);


    public void setSubject(String subject);

    public String getHtml();

    public void setHtml(String html);

    public String getText();

    public void setText(String text);

    public void setAttachments(List<Attachment> attachments);

    public int getSize();

    public void setSize(int size);

    public void setDisposition(String disposition);

    public String getDisposition();

    public void setEncoding(String encoding);

    public String getEncoding();

    public void setContentLanguage(String contentLanguage);

    public String getContentLanguage();

    public Map<String, String> getHeaders();

    public void setHeaders(Map<String, String> headers);

    public List<MailboxAddress> getTo();

    public void setTo(List<MailboxAddress> to);

    public List<MailboxAddress> getCc();

    public void setCc(List<MailboxAddress> cc);

    public List<MailboxAddress> getBcc();

    public void setBcc(List<MailboxAddress> bcc);

    /**
     *
     * @return - creates and returns a new instance of StandardMesssage suitable
     * for use as an attached message
     */
    public StandardMessage instantiateAttachedMessage();

    
}
