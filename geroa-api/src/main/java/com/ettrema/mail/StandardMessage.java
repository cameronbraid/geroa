package com.ettrema.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class StandardMessage {

    private final static Logger log = LoggerFactory.getLogger(StandardMessage.class);

    public static StandardMessage parse(MimeMessage mm) throws IOException, MessagingException {
        String text = "";
        String html = "";
        List<Attachment> attachments = new ArrayList<Attachment>();
        Object o = mm.getContent();
        if (o instanceof String) {
            text = (String) o;
        } else if (o instanceof MimeMultipart) {
            MimeMultipart multi = (MimeMultipart) o;
            for (int i = 0; i < multi.getCount(); i++) {
                BodyPart bp = multi.getBodyPart(i);
                String disp = bp.getDisposition();
                if ((disp != null) && (disp.equals(Part.ATTACHMENT) || disp.equals(Part.INLINE))) {
                    addAttachment(bp, attachments);
                } else {
                    String ct = bp.getContentType();
                    if (ct.contains("html")) {
                        html += getStringContent(bp);
                    } else if (ct.contains("text")) {
                        text += getStringContent(bp);
                    } else {  // binary
                        addAttachment(bp, attachments);
                    }
                }
            }
        } else {
            log.warn("Unknown content type: " + o.getClass() + ". expected string or MimeMultipart");
        }
        return new StandardMessage(html, text, attachments);
    }

    private static void addAttachment(BodyPart bp, List<Attachment> attachments) {
        FileSystemAttachment att = FileSystemAttachment.parse(bp);
        attachments.add(att);
    }

    private static String getStringContent(BodyPart bp) {
        try {
            Object o2 = bp.getContent();
            if (o2 == null) {
                return "";
            }
            if (o2 instanceof String) {
                return (String) o2;
            } else {
                log.warn("Unknown content type: " + o2.getClass());
                return o2.toString();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }

    }


    private String html;
    private String text;
    private List<Attachment> attachments;

    public StandardMessage(String html, String text, List<Attachment> attachments) {
        this.html = html;
        this.text = text;
        this.attachments = attachments;
    }

    public StandardMessage(String text) {
        this(null, text, null);
    }

    public String getHtmlContent() {
        return html;
    }

    public String getTextContent() {
        return text;
    }

    public Collection<Attachment> getAttachments() {
        return attachments;
    }
}
