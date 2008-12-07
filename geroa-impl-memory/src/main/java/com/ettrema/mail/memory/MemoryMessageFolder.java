package com.ettrema.mail.memory;

import com.ettrema.mail.Message;
import com.ettrema.mail.MessageFolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class MemoryMessageFolder implements MessageFolder{

    List<Message> messages = new ArrayList<Message>();

    public Collection<Message> getMessages() {
        return messages;
    }

    public int numMessages() {
        return messages.size();
    }

    public int totalSize() {
        int i = 0;
        for( Message m : messages ) {
            i += m.size();
        }
        return i;
    }
}
