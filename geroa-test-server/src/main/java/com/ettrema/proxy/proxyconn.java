package com.ettrema.proxy;

import java.net.Socket;

/** proxyconn is glue between two Sockets wrapped in dataconn, to pss traffic between them. **/
class proxyconn implements Runnable {

    public boolean debug = false;
    protected Thread t;
    protected dataconn c1,  c2;

    public proxyconn() {
    }

    public proxyconn(dataconn _c1, dataconn _c2) {
        c1 = _c2;
        c2 = _c2;
    }

    public proxyconn(Socket s1, Socket s2) {
        c1 = new dataconn(s1);
        c2 = new dataconn(s2);
    }

    public proxyconn(Socket s1, String thost, int tport) {
        c1 = new dataconn(s1);
        c2 = new dataconn(thost, tport);
    }

    public void go() {
        c1.debug = debug;
        c1.debugname = "c1";
        c2.debug = debug;
        c2.debugname = "c2";
        t = new Thread(this);
        t.start();
    }

    protected void log(boolean fromc1, byte[] d) {
    }

    public void run() {
        while (dopolling()) {
        }
    }

    /* dopolling is called in run() loop. return false to close proxy connection **/
    protected boolean dopolling() {
        try {
            byte[] d;
            d = c1.read();
            if (d != null) {
                log(true, d);
                c2.write(d);
            }
            d = c2.read();
            if (d != null) {
                log(false, d);
                c1.write(d);
            }
            if (c1.error || c2.error) {
                return false;
            }
        } catch (Throwable T) {
            exception(T);
            return false;
        }
        return true;
    }

    protected void exception(Throwable T) {
        System.err.println("proxyconn ERR" + T.getMessage());
    }
}
