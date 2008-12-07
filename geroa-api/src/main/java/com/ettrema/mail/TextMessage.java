package com.ettrema.mail;

import java.util.Collection;

/**
 *
 */
public interface TextMessage extends Message {
    public String getTextContent();
    public Collection<Attachment> getAttachments();
}
