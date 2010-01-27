package com.ettrema.proxy;

/**
 *
 */
public class RemoteProxy {
    public static void main(String[] args) {
        proxylogserver server = new proxylogserver("mail.messagingengine.com", 110, 130);
        server.go();
    }
}
