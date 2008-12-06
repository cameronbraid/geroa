package com.ettrema.mail;

/**
 *
 * @author brad
 */
public interface Message {

    public void delete();
    public int getId();
    public String getContent();
    public int size();
}
