package com.ettrema.proxy;

import com.ettrema.mail.mock.GeroaTestServer;

/**
 * Starts a logging proxy on port 110, which redirects requests to port 120
 *
 */
public class LocalProxy {
    public static void main(String[] args) {
        proxylogserver proxy = new proxylogserver("localhost", 110, 120);
        proxy.go();

        GeroaTestServer server = new GeroaTestServer();
        server.start();

    }
}
