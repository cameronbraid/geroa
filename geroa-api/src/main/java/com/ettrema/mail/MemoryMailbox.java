package com.ettrema.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;

/**
 *
 */
public class MemoryMailbox implements Mailbox{

    String password;
    Map<String,MessageFolder> folders;

    public MemoryMailbox() {
        folders = new HashMap<String, MessageFolder>();
        addFolder("inbox");
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

    public void addFolder(String name) {
        MemoryMessageFolder folder = new MemoryMessageFolder();
        folders.put(name,folder);
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
            //for( me)
            return size;
        }

    }

    public class MemoryMessageResource implements MessageResource {

        MemoryMessageFolder folder;
        Object data;

        public MemoryMessageResource(MemoryMessageFolder folder, Object data) {
            this.folder = folder;
            this.data = data;
        }

        public void delete() {
            folder.messages.remove(this);
        }

        public int size() {
            return 10; // todo
        }

    }

}
