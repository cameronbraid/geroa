package com.ettrema.proxy;

/**
 *
 */
public class FastmailWebDavProxy {
    public static void main(String[] args) {
        proxylogserver proxy = new proxylogserver("dav.messagingengine.com", 80, 80);
        proxy.go();
    }
}
