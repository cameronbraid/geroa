package com.ettrema.mail.pop;

import com.ettrema.mail.Mailbox;
import com.ettrema.mail.MailboxAddress;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthState extends BaseState {
    private final static Logger log = LoggerFactory.getLogger(AuthState.class);

    String user;
    String pass;
    Mailbox mbox;

    public AuthState(PopSession popSession) {
        super(popSession);
    }
    
    public void enter(IoSession session, PopSession popSession) {
        log.debug("now in authstate");
    }

    public void exit(IoSession session, PopSession popSession) {
    }

    public void capa(IoSession session, PopSession popSession, String[] args) {
        log.info("capa:");
        popSession.reply(session, "+OK Capability list follows");
        popSession.reply(session, "USER");
        popSession.reply(session, ".");
    }

    public void user(IoSession session, PopSession popSession, String[] args) {
        user = args[1];
        log.debug("user: " + user);
        MailboxAddress add;
        try {
            add = MailboxAddress.parse(user);
            mbox = popSession.resourceFactory.getMailbox(add);
            if (mbox != null) {
                popSession.reply(session, "+OK");
            } else {
                log.debug("mailbox not found: " + add);
                popSession.reply(session, "-ERR");
            }
        } catch (IllegalArgumentException ex) {
            popSession.reply(session, "-ERR Could not parse user name. Use form: user@domain.com");
        }
    }

    public void pass(IoSession session, PopSession popSession, String[] args) {
        if( args.length > 1 ) {
            pass = args[1];
        } else {
            pass = "";
        }
        log.debug("pass: " + pass);
        if (mbox == null) {
            log.debug("no current mailbox");
            popSession.reply(session, "-ERR");
        } else {
            if (mbox.authenticate(pass)) {
                popSession.reply(session, "+OK Mailbox locked and ready SESSIONID=<slot111-3708-1233538479-1>");
                // s("+OK Mailbox locked and ready SESSIONID=<slot111-3708-1233538479-1>\015\012");

                popSession.auth = this;
                popSession.transitionTo(session, new TransactionState(popSession));
            } else {
                log.debug("authentication failed");
                popSession.reply(session, "-ERR");
                popSession.auth = null;
            }
        }
    }

    public void apop(IoSession session, PopSession popSession, String[] args) {
        user = args[1];
        log.debug("apop: " + user);
        MailboxAddress add;
        try {
            add = MailboxAddress.parse(user);
            mbox = popSession.resourceFactory.getMailbox(add);
            if (mbox != null) {
                String md5Pass = args[2];
                if (mbox.authenticateMD5(md5Pass.getBytes())) {
                    popSession.reply(session, "+OK");
                    popSession.auth = this;
                    popSession.transitionTo(session, new TransactionState(popSession));
                } else {
                    popSession.reply(session, "-ERR authentication failed");
                }
            } else {
                popSession.reply(session, "-ERR mailbox not found");
            }
        } catch (IllegalArgumentException ex) {
            popSession.reply(session, "-ERR Could not parse user name. Use form: user@domain.com");
        }
    }

    public void auth(IoSession session, PopSession popSession, String[] args) {
        popSession.reply(session, "-ERR not supported");
    }
}