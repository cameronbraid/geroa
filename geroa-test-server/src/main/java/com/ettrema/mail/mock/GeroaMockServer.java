package com.ettrema.mail.mock;

import com.ettrema.mail.MailServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class GeroaMockServer {
    ApplicationContext context;
    private MailServer mailServer;

    public static void main(String[] args) {
        GeroaMockServer server = new GeroaMockServer();
        server.start();
    }

    public GeroaMockServer() {

        context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        mailServer = (MailServer) context.getBean("mailServer");
        mailServer.start();
        System.out.println("mock server is running on smtp port: " + mailServer.getSmtpServer().getSmtpPort() + " and pop port: " + mailServer.getPopServer().getPopPort() );
}



    private void start() {

    }
}
