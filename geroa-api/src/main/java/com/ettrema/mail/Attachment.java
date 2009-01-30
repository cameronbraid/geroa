package com.ettrema.mail;

/**
 *  Represents an attachment to an email
 */
public interface Attachment {
    /**
     *
     * @return - the name of this item within its message
     */
    String getName();

    /**
     * Use the data of the attachment. The implementation will open an input
     * stream, provide it to the given closure/callback, and the close it and
     * release any resources after the method has completed
     *
     * Do not close the stream
     *
     * @param exec - closure which will consume the stream of data
     */
    void useData(InputStreamConsumer exec);
}
