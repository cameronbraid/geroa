package com.ettrema.mail;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 */
public interface  StandardMessageFactory {

    MimeMessage toMimeMessage(StandardMessage sm, Session session);

    StandardMessage toStandardMessage(MimeMessage mm);
}
