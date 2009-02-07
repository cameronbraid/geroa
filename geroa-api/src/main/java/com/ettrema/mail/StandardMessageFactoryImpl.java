package com.ettrema.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class StandardMessageFactoryImpl implements StandardMessageFactory {

    private final static Logger log = LoggerFactory.getLogger(StandardMessageFactoryImpl.class);

    public StandardMessage toStandardMessage(MimeMessage mm) {
        StandardMessage sm = new StandardMessage();
        try {
            sm.setFrom(findFromAddress(mm));
            sm.setReplyTo(findReplyTo(mm));
            sm.setSubject(findSubject(mm));
            sm.setDisposition(mm.getDisposition());
            sm.setEncoding(mm.getEncoding());
            sm.setContentLanguage(findContentLanguage(mm.getContentLanguage()));

            sm.setTo(findRecips(mm, RecipientType.TO));
            sm.setCc(findRecips(mm, RecipientType.CC));
            sm.setBcc(findRecips(mm, RecipientType.BCC));
            sm.setSize(mm.getSize());
            Map<String, String> headers = findHeaders(mm);
            sm.setHeaders(headers);

            Object o = mm.getContent();
            if (o instanceof String) {
                String text = (String) o;
                sm.setText(text);
            } else if (o instanceof MimeMultipart) {
                log.debug("is multipart");
                MimeMultipart multi = (MimeMultipart) o;
                populateMultiPart(multi,sm);

            } else {
                log.warn("Unknown content type: " + o.getClass() + ". expected string or MimeMultipart");
            }            

            return sm;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void populateMultiPart(MimeMultipart multi, StandardMessage sm) throws IOException, MessagingException {
        System.out.println("-------------------- populateMultiPart -------------------------");
        String text = "";
        String html = "";
        System.out.println("parts: " + multi.getCount());
        for (int i = 0; i < multi.getCount(); i++) {
            BodyPart bp = multi.getBodyPart(i);
            String disp = bp.getDisposition();
            log.debug("disp: " + disp);
            if ((disp != null) && (disp.equals(Part.ATTACHMENT) || disp.equals(Part.INLINE))) {
                log.debug("..is attachment");
                addAttachment(bp, sm.getAttachments());
            } else {
                String ct = bp.getContentType();
                log.debug("..content type: " + ct);
                if (ct.contains("html")) {
                    html += getStringContent(bp);
                } else if (ct.contains("text")) {
                    text += getStringContent(bp);
                } else if (ct.contains("multipart")) {
                    Object subMessage = bp.getContent();
                    System.out.println("..found sub mesage: " + subMessage.getClass()); // TODOOO
                    if( subMessage instanceof MimeMultipart) {
                        MimeMultipart child = (MimeMultipart) subMessage;
                        StandardMessage smSub = sm.instantiateAttachedMessage();
                        populateMultiPart(child, smSub);
                        sm.getAttachedMessages().add(smSub);
                    } else {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11 unknown sub message type");
                    }
                } else {
                    // binary
                    addAttachment(bp, sm.getAttachments());
                }
            }
        }
        sm.setHtml(html);
        sm.setText(text);
    }

    void addAttachment(BodyPart bp, List<Attachment> attachments) {
        FileSystemAttachment att = FileSystemAttachment.parse(bp);
        attachments.add(att);
    }

    Map<String, String> findHeaders(MimeMessage mm) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            Enumeration en = mm.getAllHeaders();
            while (en.hasMoreElements()) {
                Object o = en.nextElement();
                Header header = (Header) o;
                map.put(header.getName(), header.getValue());
            }
            return map;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    void fillRecipients(List<MailboxAddress> to, Address[] recipients) {
        for (Address a : recipients) {
            MailboxAddress ma = MailboxAddress.parse(a.toString());
            to.add(ma);
        }
    }

    void fillContentLanguage(String contentLanguage, MimeMessage mm) {
        try {
            if (contentLanguage == null) {
                return;
            }
            String[] arr = {contentLanguage};
            mm.setContentLanguage(arr);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void fillContent(StandardMessage sm, MimeMessage mm) {
        try {
            MimeMultipart multipart = new MimeMultipart("related");
            if (sm.getText() != null) {
                BodyPart bp = new MimeBodyPart();
                bp.setContent(sm.getText(), "text/plain");
                multipart.addBodyPart(bp);
            }
            if( sm.getHtml() != null ) {
                BodyPart bp = new MimeBodyPart();
                bp.setContent(sm.getHtml(), "text/html");
                multipart.addBodyPart(bp);
            }
            if( sm.getAttachments() != null && sm.getAttachments().size() > 0 ) {
                // todo
            }
            if( sm.getAttachedMessages() != null && sm.getAttachedMessages().size() > 0 ) {
                // todo
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void fillReplyTo(StandardMessage sm, MimeMessage mm) {
        try {
            MailboxAddress ma = sm.getReplyTo();
            if (ma == null) {
                return;
            }
            Address[] addresses = new Address[1];
            addresses[0] = ma.toInternetAddress();
            mm.setReplyTo(addresses);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private MailboxAddress findReplyTo(MimeMessage mm) {
        try {
            return findSingleAddress(mm.getReplyTo());
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    MailboxAddress findFromAddress(MimeMessage mm) {
        try {
            return findSingleAddress(mm.getFrom());
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    MailboxAddress findSingleAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        return MailboxAddress.parse(addresses[0].toString());
    }

    String findContentLanguage(String[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        return arr[0];
    }

    String findSubject(MimeMessage mm) {
        try {
            return mm.getSubject();
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    String getStringContent(BodyPart bp) {
        System.out.println("getStringContent");
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

    List<MailboxAddress> findRecips(MimeMessage mm, RecipientType type) {
        try {
            Address[] recips = mm.getRecipients(type);
            List<MailboxAddress> list = new ArrayList<MailboxAddress>();
            if (recips != null) {
                for (Address a : recips) {
                    MailboxAddress mba = MailboxAddress.parse(a.toString());
                    list.add(mba);
                }
            }
            return list;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public MimeMessage toMimeMessage(StandardMessage sm, Session session) {
        try {
            MimeMessage mm = new MimeMessage(session);

            mm.setFrom(sm.getFrom().toInternetAddress());
            fillReplyTo(sm, mm);
            mm.setSubject(sm.getSubject());
            mm.setDisposition(sm.getDisposition());
            fillContentLanguage(sm.getContentLanguage(), mm);

            fillRecipients(sm.getTo(), mm.getRecipients(RecipientType.TO));
            fillRecipients(sm.getCc(), mm.getRecipients(RecipientType.CC));
            fillRecipients(sm.getBcc(), mm.getRecipients(RecipientType.BCC));

            fillContent(sm, mm);


            return mm;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
