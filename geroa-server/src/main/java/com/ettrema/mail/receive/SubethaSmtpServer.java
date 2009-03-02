
package com.ettrema.mail.receive;

import com.ettrema.mail.AcceptEvent;
import com.ettrema.mail.DeliverEvent;
import com.ettrema.mail.Event;
import com.ettrema.mail.Filter;
import com.ettrema.mail.FilterChain;
import com.ettrema.mail.LoginEvent;
import com.ettrema.mail.send.MailSender;
import com.ettrema.mail.MailResourceFactory;
import com.ettrema.mail.Mailbox;
import com.ettrema.mail.MailboxAddress;
import com.sun.mail.smtp.SMTPMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.auth.LoginAuthenticationHandler;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.PlainAuthenticationHandler;
import org.subethamail.smtp.auth.PluginAuthenticationHandler;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.CommandHandler;
import org.subethamail.smtp.server.MessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

public class SubethaSmtpServer implements MessageListener, SmtpServer {
    private final static Logger log = LoggerFactory.getLogger(SubethaSmtpServer.class);
        
    private SMTPServer smtpReceivingServer;
    private final int smtpPort;
    private final boolean enableTls;
    private final MailResourceFactory resourceFactory;
    private final MailSender mailSender;
    private final List<Filter> filters;

    public SubethaSmtpServer(int smtpPort, boolean enableTls, MailResourceFactory resourceFactory, MailSender mailSender, List<Filter> filters) {
        this.smtpPort = smtpPort;
        this.enableTls = enableTls;
        this.resourceFactory = resourceFactory;
        this.mailSender = mailSender;
        this.filters = filters;
    }

    public SubethaSmtpServer(MailResourceFactory resourceFactory, MailSender mailSender, List<Filter> filters) {
        this(25,false,resourceFactory,mailSender, filters);
    }

    
    public void start() {
        initSmtpReceiver();

        log.info("starting SMTP server on port: " + this.smtpReceivingServer.getPort() + " address: " + this.smtpReceivingServer.getBindAddress());
        try {
            this.smtpReceivingServer.start();
        } catch (Throwable e) {
            throw new RuntimeException("Exception starting SMTP server. port: " + this.smtpReceivingServer.getPort() + " address: " + this.smtpReceivingServer.getBindAddress(), e);
        }
        log.info("Conjola email server started.");
    }

    public void stop() {
        smtpReceivingServer.stop();
        smtpReceivingServer = null;
    }

    private String getSubjectDontThrow(MimeMessage mm) {
        try {
            return mm.getSubject();
        } catch (MessagingException ex) {
            return "[couldnt_read_subject]";
        }
    }
    
    private void initSmtpReceiver() {
        Collection<MessageListener> listeners = new ArrayList<MessageListener>(1);
        listeners.add(this);

        if( enableTls ) {
            log.info("Creating TLS enabled server");
            this.smtpReceivingServer = new SMTPServer(listeners);
        } else {
            log.info("Creating TLS DIS-abled server");
            this.smtpReceivingServer = new TlsDisabledSmtpServer(listeners);
        }
        this.smtpReceivingServer.setPort(smtpPort);
        this.smtpReceivingServer.setMaxConnections(30000);
        CommandHandler cmdHandler = this.smtpReceivingServer.getCommandHandler();

        MessageListenerAdapter mla = (MessageListenerAdapter) smtpReceivingServer.getMessageHandlerFactory();
        mla.setAuthenticationHandlerFactory(null);
//        mla.setAuthenticationHandlerFactory(new AuthHandlerFactory());
    }
    
    /**
     * 
     * @return - the session used by the mail sender. can be used to build smtpmessage objects
     */
    public Session getSmtpSendSession() {
        return mailSender.getSession();
    }

    
    /**
     * Sends the message assuming that this mimemessage was constructed on the MailSender's
     * session
     * 
     * @param mm
     */
    public void sendMail(MimeMessage mm) {
        mailSender.sendMail(mm);
    }
    
    public void sendMail(String fromAddress, String fromPersonal,List<String> to, String replyTo, String subject, String text) {
        mailSender.sendMail(fromAddress, fromPersonal, to, replyTo, subject, text);
    }
    

            
    /**
     * Subetha.MessageListener
     * 
     * Always accept everything when receiving SMTP messages
     */
    public boolean accept(String sFrom, String sRecipient) {
        log.debug("accept? " + sFrom + " - " +sRecipient);
        if( sFrom == null || sFrom.length() == 0 ) {
            log.error("Cannot accept email with no from address. Recipient is: " + sRecipient);
            return false;
        }
        final AcceptEvent event = new AcceptEvent(sFrom, sRecipient);
        Filter terminal = new Filter() {

            public void doEvent(FilterChain chain, Event e) {
                MailboxAddress from = MailboxAddress.parse(event.getFrom());
                Mailbox fromMailbox = resourceFactory.getMailbox(from);
                if (fromMailbox != null && !fromMailbox.isEmailDisabled() ) {
                    event.setAccept(true);
                    return ;
                }
                MailboxAddress recip = MailboxAddress.parse(event.getRecipient());
                Mailbox recipMailbox = resourceFactory.getMailbox(recip);

                boolean b = (recipMailbox != null && !recipMailbox.isEmailDisabled());
                log.debug("accept email from: " + event.getFrom() + " to: " + event.getRecipient() + "?" + b);
                event.setAccept(b);
            }
        };
        FilterChain chain = new FilterChain(filters, terminal);
        chain.doEvent(event);
        return event.isAccept();
    }

    /**
     * Subetha MessageListener. Called when an SMTP message has bee received. Could
     * be a send request from our domain or an email to our domain
     * 
     */
    public void deliver(String sFrom, String sRecipient, final InputStream data) throws TooMuchDataException, IOException {
        log.debug("deliver email from: " + sFrom + " to: " + sRecipient);
        log.debug("email from: " + sFrom + " to: " + sRecipient);
        final DeliverEvent event = new DeliverEvent(sFrom, sRecipient, data);
        Filter terminal = new Filter() {

            public void doEvent(FilterChain chain, Event e) {
                MailboxAddress from = MailboxAddress.parse(event.getFrom());
                MailboxAddress recip = MailboxAddress.parse(event.getRecipient());

                MimeMessage mm = parseInput(data);

                Mailbox recipMailbox = resourceFactory.getMailbox(recip);
                if (recipMailbox != null && !recipMailbox.isEmailDisabled()) {
                    log.debug("recipient is known to us, so store: " + recip);
                    storeMail(recipMailbox,mm);
                } else {
                    Mailbox fromMailbox = resourceFactory.getMailbox(from);
                    if (fromMailbox != null && !fromMailbox.isEmailDisabled() ) {
                        log.debug("known from address, so will transmit: from: " + from);
                        mailSender.sendMail(mm);
                    } else {
                        throw new NullPointerException("Neither from address nor recipient are known to us. Will not store or send: from: " + event.getFrom() + " to: " + event.getRecipient());
                    }
                }

            }
        };
        FilterChain chain = new FilterChain(filters, terminal);
        chain.doEvent(event);
    }

    protected MimeMessage parseInput(InputStream data) {
        try {
            return new SMTPMessage(getSession(), data);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    /**
     * Creates the JavaMail Session object for use in WiserMessage
     */
    protected Session getSession() {
        return mailSender.getSession();
    }

    private void storeMail(Mailbox recipMailbox, MimeMessage mm) {
        try {
            recipMailbox.storeMail(mm);
        } catch (Throwable e) {
            String subject = getSubjectDontThrow(mm);
            log.error("Exception storing mail. mailbox: " + recipMailbox.getClass() + " message: " + subject,e);
        }
    }

    /**
     * Creates the AuthHandlerFactory which logs the user/pass.
     */
    public class AuthHandlerFactory implements AuthenticationHandlerFactory {

        public AuthenticationHandler create() {
            PluginAuthenticationHandler ret = new PluginAuthenticationHandler();
            UsernamePasswordValidator validator = new UsernamePasswordValidator() {

                public void login(String username, String password) throws LoginFailedException {
                    boolean loginOk = doLogin(username, password);
                    if (!loginOk) {
                        throw new LoginFailedException("authentication failed");
                    }

                }
            };
            ret.addPlugin(new PlainAuthenticationHandler(validator));
            ret.addPlugin(new LoginAuthenticationHandler(validator));
            return ret;
        }
    }
    
    public class MySmtpMessage extends SMTPMessage {
        public MySmtpMessage(Session session, MimeMessage mm) throws MessagingException {
            super(mm);
            this.session = session;
        }
        
    }

    public boolean doLogin(String username, String password) {
        final LoginEvent event = new LoginEvent(username, password);
        Filter terminal = new Filter() {

            public void doEvent(FilterChain chain, Event e) {
                event.setLoginSuccessful( _doLogin(event.getUsername(), event.getPassword()) );
            }
        };
        FilterChain chain = new FilterChain(filters, terminal);
        chain.doEvent(event);
        return event.isLoginSuccessful();
    }

    public boolean _doLogin(String username, String password) {
        try {
            MailboxAddress userName = MailboxAddress.parse(username);
            Mailbox mbox = resourceFactory.getMailbox(userName);
            if (mbox == null) {
                log.debug("user not found");
                return false;
            }
            if (!mbox.authenticate(password)) {
                log.debug("authentication failed");
                return false;
            }
            return true;
        } catch (IllegalArgumentException ex) {
            log.debug("username could not be parsed. use form user@domain.com");
            return false;
        }

    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public boolean isEnableTls() {
        return enableTls;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public MailResourceFactory getResourceFactory() {
        return resourceFactory;
    }
}
