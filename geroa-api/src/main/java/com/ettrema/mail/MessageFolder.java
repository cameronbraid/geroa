package com.ettrema.mail;

import java.util.Collection;

/**
 *
 * @author brad
 */
public interface MessageFolder {
    
    /**
     * 
     * @return - collection of all messages in this folder
     */
    public Collection<Message> getMessages();

    /**
     * 
     * @return -  number of messages in this folder
     */
    public int numMessages();

    /**
     * 
     * @return - sum of octet size of all messages in this folder
     */
    public int totalSize();
}
