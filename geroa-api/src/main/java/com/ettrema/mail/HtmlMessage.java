package com.ettrema.mail;

import java.util.Collection;

/**
 *
 */
public interface HtmlMessage {
    String getHtmlContent();
    public Collection<Attachment> getAttachments();
    
}
