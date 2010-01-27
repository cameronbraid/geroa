package com.ettrema.proxy;

/**
 *
 */
public class LocalWebDavProxy {
    public static void main(String[] args) {
        proxylogserver proxy = new proxylogserver("localhost", 80, 8080);
        proxy.go();
    }
}
