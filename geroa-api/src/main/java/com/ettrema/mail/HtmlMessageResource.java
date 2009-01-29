package com.ettrema.mail;

import java.util.Collection;

/**
 *
 */
public interface HtmlMessageResource extends MessageResource {
    String getHtmlContent();
    public Collection<Attachment> getAttachments();
    
}
