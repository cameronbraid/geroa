package com.ettrema.mail;

import java.io.OutputStream;

/**
 *  The minimal interface needed to support basic email functionality. The
 * interface allows a resource to identify its size, to be deleted, and to be
 *  written to a client
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
    int getSize();

    /**
     * write the message in mime format to the given output stream
     *
     * this will usually be implemented as mimeMessage.writeTo(out);
     *
     * @param out
     */
    void writeTo(OutputStream out);
}
