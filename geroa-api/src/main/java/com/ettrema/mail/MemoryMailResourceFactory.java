package com.ettrema.mail;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MemoryMailResourceFactory implements MailResourceFactory{

    final Map<String,Mailbox> mailboxes;

    public MemoryMailResourceFactory() {
        mailboxes = new HashMap<String, Mailbox>();
    }

    public MemoryMailResourceFactory(Map<String, Mailbox> mailboxes) {
        this.mailboxes = mailboxes;
    }
    
    public Mailbox getMailbox(MailboxAddress add) {
        return mailboxes.get(add.user);
    }

    public void addMailbox(Mailbox mailbox, String name) {
        mailboxes.put(name, mailbox);
    }
}
