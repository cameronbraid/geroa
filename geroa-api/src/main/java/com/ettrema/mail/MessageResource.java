package com.ettrema.mail;

import javax.mail.internet.MimeMessage;

/**
 *
 */
public interface MessageResource {
    public void delete();

    public int size();

    MimeMessage getMimeMessage();
}
