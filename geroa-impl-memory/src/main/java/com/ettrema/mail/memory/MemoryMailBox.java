package com.ettrema.mail.memory;

import com.ettrema.mail.Mailbox;
import com.ettrema.mail.MessageFolder;
import com.ettrema.mail.MessageResource;
import com.ettrema.mail.StandardMessage;
import com.ettrema.mail.StandardMessageFactory;
import com.ettrema.mail.StandardMessageFactoryImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class MemoryMailBox implements Mailbox{

    private final static Logger log = LoggerFactory.getLogger(MemoryMailBox.class);

    private static final StandardMessageFactory factory = new StandardMessageFactoryImpl();

    String password;
    Map<String,MessageFolder> folders;

    public MemoryMailBox() {
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

        try {
            File f = new File("c:\\test.smtp");
            FileOutputStream fos = new FileOutputStream(f);
            mm.writeTo(fos);
            fos.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
        }

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
        StandardMessage message;

        public MemoryMessageResource(MemoryMessageFolder folder, MimeMessage mimeMessage) {
            this.folder = folder;
            this.message = factory.toStandardMessage(mimeMessage);
            if( message.getText() == null || message.getText().length() == 0 ) throw new IllegalArgumentException("no text content");
        }

        public void delete() {
            folder.messages.remove(this);
        }

        public int size() {
            int i = message.size();
            return i;
        }

        public void writeTo(OutputStream out) {
            message.writeTo(out);
        }
    }
}
