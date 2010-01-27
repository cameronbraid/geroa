package com.ettrema.mail;


public interface Filter {

    public void doEvent(FilterChain chain, Event event);

}
