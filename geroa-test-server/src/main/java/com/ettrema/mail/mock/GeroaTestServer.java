package com.ettrema.mail.mock;

import com.ettrema.mail.MailServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 ** Demonstrates initialising the Geroa Server using spring, and then starting it
 */
public class GeroaTestServer {

    ApplicationContext context;
    private MailServer mailServer;

    public static void main(String[] args) {
        GeroaTestServer server = new GeroaTestServer();
        server.start();
    }

    public GeroaTestServer() {
    }

    public void start() {
        context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        mailServer = (MailServer) context.getBean("mailServer");
        mailServer.start();
        System.out.println("mock server is running on smtp port: " + mailServer.getSmtpServer().getSmtpPort() + " and pop port: " + mailServer.getPopServer().getPopPort());
    }
}
