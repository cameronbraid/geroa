package com.ettrema.mail;

import java.io.OutputStream;

/**
 *
 */
public interface MessageResource {
    /**
     * physically delete the resource
     */
    void delete();

    /**
     *
     *
     * @return - the size of the message when formatted as a mime message
     */
    int size();

    /**
     * write the message in mime format to the given output stream
     *
     * this will usually be implemented as mimeMessage.writeTo(out);
     *
     * @param out
     */
    void writeTo(OutputStream out);
}
