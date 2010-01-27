package com.ettrema.proxy;

import java.net.Socket;

/** proxylogserver is a logging version of proxyserver.
Stores log files in "log' subdirectory **/
public class proxylogserver extends proxyserver {

    public proxylogserver() {
    }

    public proxylogserver(String targetHost, int targetPort, int listenPort) {
        super(targetHost, targetPort, listenPort);
    }

    @Override
    public void go() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void gotconn(Socket sconn) throws Exception {
        proxylogconn pc = new proxylogconn(sconn, targetHost, targetPort);
        pc.go();
    }

    public static void main(String args[]) {
        String targetHost = args[0];
        Integer targetPort = new Integer(args[1]);
        Integer listenPort = new Integer(args[2]);
        proxylogserver us = new proxylogserver(targetHost, targetPort, listenPort);
        us.go();
    }
}

