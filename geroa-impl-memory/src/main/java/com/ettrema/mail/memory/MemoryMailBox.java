package com.ettrema.mail.memory;

import com.ettrema.mail.MailUtils;
import com.ettrema.mail.Mailbox;
import com.ettrema.mail.MessageFolder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.MimeMessage;

/**
 *
 */
public class MemoryMailBox implements Mailbox{

    String name;
    String password;
    boolean disabled;

    Map<String,MemoryMessageFolder> folders = new HashMap<String,MemoryMessageFolder>();

    public MemoryMailBox(String name, String password) {
        this.name = name;
        this.password = password;
        this.folders.put("inbox", new MemoryMessageFolder());
    }



    public boolean authenticate(String password) {
        if( this.password == null ) {
            return password==null;
        } else {
            return this.password.equals(password);
        }
    }

    public boolean authenticateMD5(byte[] passwordHash) {
        try {
            byte[] actual = MailUtils.md5Digest(this.password);
            return java.util.Arrays.equals(actual, passwordHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public MessageFolder getInbox() {
        return folders.get("inbox");
    }

    public MessageFolder getMailFolder(String name) {
        return folders.get(name);
    }

    public boolean isEmailDisabled() {
        return disabled;
    }

    public void storeMail(MimeMessage mm) {
        getInbox().getMessages().add(mm);
    }

}
