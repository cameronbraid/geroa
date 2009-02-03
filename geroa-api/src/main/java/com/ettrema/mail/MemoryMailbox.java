package com.ettrema.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MemoryMailbox implements Mailbox{

    private final static Logger log = LoggerFactory.getLogger(MemoryMailbox.class);

    String password;
    Map<String,MessageFolder> folders;

    public MemoryMailbox() {
        folders = new HashMap<String, MessageFolder>();
        MemoryMessageFolder folder = addFolder("inbox");
//        for( int i=0; i<50; i++) {
//            addMockMessage(folder, "hi there " + i); // todo: move this to test config
//        }
        this.password = "password";
    }

    public boolean authenticate(String password) {
        return password.equals(this.password);
    }

    public boolean authenticateMD5(byte[] passwordHash) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MessageFolder getInbox() {
        return folders.get("inbox");
    }

    public MessageFolder getMailFolder(String name) {
        return folders.get(name);
    }

    public boolean isEmailDisabled() {
        return false;
    }

    public void storeMail(MimeMessage mm) {
        MemoryMessageFolder folder = (MemoryMessageFolder) getInbox();
        MemoryMessageResource res = new MemoryMessageResource(folder, mm);
        folder.messages.add(res);
    }

    public MemoryMessageFolder addFolder(String name) {
        MemoryMessageFolder folder = new MemoryMessageFolder();
        folders.put(name,folder);
        return folder;
    }

    private void addMockMessage(MemoryMessageFolder folder, String subject) {
        try {
            MimeMessage msg = new MimeMessage((Session) null);
            msg.setSubject(subject);
            folder.messages.add(new MemoryMessageResource(folder, msg));
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public class MemoryMessageFolder implements MessageFolder {

        List<MessageResource> messages = new ArrayList<MessageResource>();

        public Collection<MessageResource> getMessages() {
            return messages;
        }

        public int numMessages() {
            return messages.size();
        }

        public int totalSize() {
            int size = 0;
            for( MessageResource res : messages ) {
                size += res.size();
            }
            log.debug("total size: " + size);
            return size;
        }

    }

    public class MemoryMessageResource implements MessageResource {

        MemoryMessageFolder folder;
        MimeMessage message;

        public MemoryMessageResource(MemoryMessageFolder folder, MimeMessage message) {
            this.folder = folder;
            this.message = message;
        }

        public void delete() {
            folder.messages.remove(this);
        }

        public int size() {
            try {
                int i = message.getSize();
                log.debug("message size: " + i);
                if( i < 0 ) {
                    log.warn("negative size from resource: " + i + ". returning 1 instead");
                    i = 1;
                }
                return i;
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }

        public MimeMessage getMimeMessage() {
            return message;
        }

        public void writeTo(OutputStream out) {
            try {
                message.writeTo(out);
            } catch (IOException ex) {
                log.error("exception writing data",ex);
            } catch (MessagingException ex) {
                log.error("exception writing data",ex);
            }
        }
    }
}
