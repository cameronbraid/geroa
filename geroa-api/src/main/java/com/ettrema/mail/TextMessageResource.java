package com.ettrema.mail;

import java.util.Collection;

/**
 *
 */
public interface TextMessageResource extends MessageResource {
    public String getTextContent();
    public Collection<Attachment> getAttachments();
}
