package com.ettrema.mail;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *  Interface which describes a standard message. This is a representation of
 *  an email which can have text, html and attachments. Use the StandardMessageFactory
 *  to populate these from a MimeMessage and to convert back again
 *
 *  Note that an implementation class may often wish to implement this and
 *  MessageResource
 */
public interface StandardMessage {

    /**
     * Add a new attachment to this message. This may be called multiple times during parsing, but after
     * the subject and from fields are set.
     * 
     * @param name
     * @param ct
     * @param contentId
     * @param in
     */
    public void addAttachment(String name, String ct, String contentId, InputStream in);

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
