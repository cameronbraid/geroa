package com.ettrema.mail.pop;

import com.ettrema.mail.Message;
import com.ettrema.mail.MessageFolder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionState extends BaseState {

    private final static Logger log = LoggerFactory.getLogger(TransactionState.class);
    
    MessageFolder inbox;

    TransactionState(PopSession popSession) {
        super(popSession);
        this.popSession = popSession;
        inbox = popSession.auth.mbox.getInbox();
        int num = 1;
        popSession.messageIds = new ArrayList<MessageId>();
        for (Message m : inbox.getMessages()) {
            MessageId mid = new MessageId(num++, m);
            popSession.messageIds.add(mid);
        }
    }

    private Message get(PopSession popSession, int num) {
        return popSession.messageIds.get(num).message;
    }

    public void enter(IoSession session, PopSession popSession) {
        popSession.reply(session, "+OK " + popSession.auth.user + " has " + inbox.numMessages() + " messages (" + inbox.totalSize() + " octets)");
        log.info("entering transaction state");
    }

    public void exit(IoSession session, PopSession popSession) {
    }

    public void uidl(IoSession session, PopSession popSession, String[] args) {
        if (args.length <= 1) {
            popSession.reply(session, "+OK");
            for (Message m : inbox.getMessages()) {
                popSession.reply(session, "" + m.getId() + " " + m.hashCode());
            }
            popSession.reply(session, ".");
        } else {
            String sNum = args[1];
            int num = Integer.parseInt(sNum);
            Message m = get(popSession, num);
            if (m == null) {
                popSession.reply(session, "-ERR no such message");
            } else {
                popSession.reply(session, "+OK " + m.hashCode());
            }
        }
    }

    public void list(IoSession session, PopSession popSession, String[] args) {
        if (args.length <= 1) {
            popSession.reply(session, "+OK");
            for (Message m : inbox.getMessages()) {
                popSession.reply(session, "" + m.getId() + " " + m.size());
            }
            popSession.reply(session, ".");
        } else {
            String sNum = args[1];
            int num = Integer.parseInt(sNum);
            Message m = get(popSession, num);
            if (m == null) {
                popSession.reply(session, "-ERR no such message");
            } else {
                popSession.reply(session, "+OK " + m.size());
            }
        }
    }

    public void capa(IoSession session, PopSession popSession, String[] args) {
        popSession.reply(session, "+OK Capability list follows");
        popSession.reply(session, ".");
    }

    public void stat(IoSession session, PopSession popSession, String[] args) {
        popSession.reply(session, "+OK " + popSession.messageIds.size() + " " + inbox.totalSize());
    }

    public void retr(IoSession session, PopSession popSession, String[] args) {
        String sNum = args[1];
        int num = Integer.parseInt(sNum);
        Message m = get(popSession, num);
        if (m == null) {
            popSession.reply(session, "-ERR no such message");
        } else {
            try {
                popSession.reply(session, "+OK " + m.size() + " octets");
                Session mailSess = null;
                MimeMessage mm = new MimeMessage(mailSess);
                Address add = new InternetAddress();
                mm.addRecipient(RecipientType.TO, add);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                mm.writeTo(bout);
                session.write(bout.toByteArray());
                popSession.reply(session, ".");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void dele(IoSession session, PopSession popSession, String[] args) {
        String sNum = args[1];
        int num = Integer.parseInt(sNum);
        MessageId mid = popSession.messageIds.get(num);
        if (mid != null) {
            mid.deleted = true;
            popSession.reply(session, "+OK");
            return;
        } else {
            popSession.reply(session, "-ERR no such message");
        }
    }
}
