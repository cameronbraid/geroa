package com.ettrema.mail;

import java.io.InputStream;
import java.util.concurrent.Callable;
import javax.mail.BodyPart;

/**
 *
 */
public class StandardAttachment implements Attachment {

    public StandardAttachment(BodyPart bp) {
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void useData(Callable<InputStream> exec) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
