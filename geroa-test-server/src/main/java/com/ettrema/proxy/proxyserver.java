package com.ettrema.proxy;

import java.net.ServerSocket;
import java.net.Socket;

/** proxyserver listens on given lport, forwards traffic to tport on thost **/
public class proxyserver implements Runnable {

    public boolean debug = false;
    protected int targetPort,  listenPort; // tport is port on target host, lport is listen port
    protected String targetHost;
    protected Thread thread;

    public proxyserver() {
    }


    public proxyserver(String targetHost, int targetPort, int listenPort) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.listenPort = listenPort;
    }

    public void go() {
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        System.out.println("starting listening on: " + listenPort);
        try {
            ServerSocket ss = new ServerSocket(listenPort);
            if (debug) {
                System.err.println("proxyserver: " + listenPort + " listening");
            }
            while (true) {
                Socket sconn = ss.accept();
                if (debug) {
                    System.err.print(" gotConn: " + listenPort + " ");
                }
                gotconn(sconn);
            }
        } catch (Throwable T) {
            if (debug) {
                System.err.println("proxyserver: " + listenPort + " " + T.toString());
            }
            T.printStackTrace();
        }
    }

    protected void gotconn(Socket sconn) throws Exception {
        proxyconn pc = new proxyconn(sconn, targetHost, targetPort);
        pc.go();
    }

    public static void main(String args[]) {
        String targetHost = args[0];
        Integer targetPort = new Integer(args[1]);
        Integer listenPort = new Integer(args[2]);
        proxyserver us = new proxyserver(targetHost, targetPort, listenPort);
        us.go();
    }
}

