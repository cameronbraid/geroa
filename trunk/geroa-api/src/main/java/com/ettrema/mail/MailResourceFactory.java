package com.ettrema.mail;

/**
 *
 * @author brad
 */
public interface MailResourceFactory {

    public Mailbox getMailbox(MailboxAddress add);
    
}
