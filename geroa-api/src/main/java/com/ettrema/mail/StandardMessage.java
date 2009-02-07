package com.ettrema.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class StandardMessage {

    private final static Logger log = LoggerFactory.getLogger(StandardMessage.class);

    private MailboxAddress from;
    private MailboxAddress replyTo;
    private List<MailboxAddress> to = new ArrayList<MailboxAddress>();
    private List<MailboxAddress> cc = new ArrayList<MailboxAddress>();
    private List<MailboxAddress> bcc = new ArrayList<MailboxAddress>();
    private String subject;
    private String html;
    private String text;
    private List<Attachment> attachments = new ArrayList<Attachment>();
    private List<StandardMessage> attachedMessages = new ArrayList<StandardMessage>();
    private int size;
    private String disposition;
    private String encoding;
    private String contentLanguage;
    private Map<String,String> headers;

    public List<StandardMessage> getAttachedMessages() {
        return attachedMessages;
    }

    public void setAttachedMessages(List<StandardMessage> attachedMessages) {
        this.attachedMessages = attachedMessages;
    }

    

    public String getSubject() {
        return subject;
    }
    
    public MailboxAddress getFrom() {
        return from;
    }

    public int size() {
        return size;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }


    public void writeTo(OutputStream out) {
        try {
            MimeMessage mm = toMimeMessage(null);
            mm.writeTo(out);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void fillMimeMessage(MimeMessage msg) {
        // todo
    }

    public MimeMessage toMimeMessage(Session session) {
        MimeMessage mm = new MimeMessage(session);
        fillMimeMessage(mm);
        return mm;
    }

    public void setFrom(MailboxAddress from) {
        this.from = from;
    }

    public MailboxAddress getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(MailboxAddress replyTo) {
        this.replyTo = replyTo;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getDisposition() {
        return disposition;
    }


    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public List<MailboxAddress> getTo() {
        return to;
    }

    public void setTo(List<MailboxAddress> to) {
        this.to = to;
    }

    public List<MailboxAddress> getCc() {
        return cc;
    }

    public void setCc(List<MailboxAddress> cc) {
        this.cc = cc;
    }

    public List<MailboxAddress> getBcc() {
        return bcc;
    }

    public void setBcc(List<MailboxAddress> bcc) {
        this.bcc = bcc;
    }

    /**
     *
     * @return - creates and returns a new instance of StandardMesssage suitable
     * for use as an attached message
     */
    public StandardMessage instantiateAttachedMessage() {
        StandardMessage sub = new StandardMessage();   
        return sub;
    }

    
}
