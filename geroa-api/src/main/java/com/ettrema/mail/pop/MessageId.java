package com.ettrema.mail.pop;

import com.ettrema.mail.Message;

class MessageId {

    final int messageNumber;
    final Message message;
    boolean deleted;

    public MessageId(int messageNumber, Message message) {
        super();
        this.messageNumber = messageNumber;
        this.message = message;
    }
}
