package com.ettrema.mail;

import com.ettrema.mail.pop.PopServer;
import com.ettrema.mail.receive.SmtpServer;
import com.ettrema.mail.send.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailServer {

    private final static Logger log = LoggerFactory.getLogger(MailServer.class);
    

    private MailSender mailSender;
    private SmtpServer smtpServer;
    private PopServer popServer;

    public MailServer() {
    }

    public MailServer(MailSender mailSender, SmtpServer smtpServer, PopServer popServer) {
        this.mailSender = mailSender;
        this.smtpServer = smtpServer;
        this.popServer = popServer;
    }
                   
    
    public void start() {
        log.debug("starting mail servers...");
        if( mailSender != null ) {
            log.debug("starting mail sender..");
            mailSender.start();
        }
        if( smtpServer != null ) {
            log.debug("starting smtp receiver..");
            smtpServer.start();
        }
        if( popServer != null ) {
            log.debug("starting pop server..");
            popServer.start();
        }
        log.debug("...done loading mail servers");
    }


    public void stop() {
        log.debug("stopping mail servers...");
        if( mailSender != null ) {
            mailSender.stop();
        }
        if( smtpServer != null ) {
            smtpServer.stop();
        }
        if( popServer != null ) {
            popServer.stop();
        }
        log.debug("...done stopping mail servers");
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public PopServer getPopServer() {
        return popServer;
    }

    public SmtpServer getSmtpServer() {
        return smtpServer;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setPopServer(PopServer popServer) {
        this.popServer = popServer;
    }

    public void setSmtpServer(SmtpServer smtpServer) {
        this.smtpServer = smtpServer;
    }

    
}
