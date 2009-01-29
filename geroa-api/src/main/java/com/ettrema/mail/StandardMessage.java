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

/**
 *
 */
public class StandardMessage {

    public static StandardMessage parse(MimeMessage mm) throws IOException, MessagingException {
        Object o = mm.getContent();
        if (o instanceof String) {
            String s = (String) o;
            return new StandardMessage(s);
        } else if (o instanceof MimeMultipart) {
            String s = "";
            MimeMultipart multi = (MimeMultipart) o;
            List<Attachment> attachments = new ArrayList<Attachment>();
            for (int i = 0; i < multi.getCount(); i++) {
                BodyPart bp = multi.getBodyPart(i);
                String disp = bp.getDisposition();
                if( disp == null ) {
                    // determine based on mime type
                } else if( disp.equals(Part.ATTACHMENT) || disp.equals(Part.INLINE)) {
                    // handle attachment
                } else {
                    // determine based on mime type
                }
                String ct = bp.getContentType();
                if( ct.contains("html")) {
                    Object o2 = bp.getContent();
                    if (o2 instanceof String) {
                        s += (String) o2;
                    }
                } else if(ct.contains("text")){

                } else {  // binary

                }
            }

        } else {
        }
        return null;
    }

    private String html;
    private String text;
    private List<Attachment> attachments;

    public StandardMessage(String html, String text, List<Attachment> attachments) {
        this.html = html;
        this.text = text;
        this.attachments = attachments;
    }

    public StandardMessage( String text) {
        this(null,text,null);
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
