package com.ettrema.proxy;

import com.ettrema.mail.mock.GeroaMockServer;

/**
 *
 */
public class LocalProxy {
    public static void main(String[] args) {
        proxylogserver proxy = new proxylogserver("localhost", 110, 120);
        proxy.go();

        GeroaMockServer server = new GeroaMockServer();
        server.start();

    }
}
