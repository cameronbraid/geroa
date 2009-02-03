package com.ettrema.mail.pop;

import com.bradmcevoy.io.ChunkWriter;
import com.bradmcevoy.io.ChunkingOutputStream;
import com.ettrema.mail.Message;
import com.ettrema.mail.MessageFolder;
import com.ettrema.mail.MessageResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.mail.Session;
import org.apache.mina.common.ByteBuffer;
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
        Collection<MessageResource> messageResources = inbox.getMessages();
        popSession.messages = new ArrayList<Message>();
        for (MessageResource mr : messageResources) {
            Message m = new Message(mr, num++);
            popSession.messages.add(m);
        }
    }

    private Message get(PopSession popSession, int num) {
        return popSession.messages.get(num - 1);
    }

    public void enter(IoSession session, PopSession popSession) {
        // don't know what this is doing here..
//        popSession.reply(session, "+OK " + popSession.auth.user + " has " + inbox.numMessages() + " messages (" + inbox.totalSize() + " octets)");
        log.info("entering transaction state");
    }

    public void exit(IoSession session, PopSession popSession) {
    }

    public void uidl(IoSession session, PopSession popSession, String[] args) {
        if (args.length <= 1) {
            popSession.reply(session, "+OK");
            for (Message m : popSession.messages) {
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
        log.debug("list: " + args.length);
        if (args.length <= 1) {
            popSession.reply(session, "+OK");
            for (Message m : popSession.messages) {
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
        // popSession.reply(session, "+OK " + popSession.messages.size() + " " + inbox.totalSize());
        popSession.reply(session, "+OK " + popSession.messages.size() + " 123450");
    }

    public void retr(final IoSession session, PopSession popSession, String[] args) {
        String sNum = args[1];
        int num = Integer.parseInt(sNum);
        Message m = get(popSession, num);

        if (m == null) {
            popSession.reply(session, "-ERR no such message");
        } else {
            try {
                popSession.reply(session, "+OK " + m.size() + " octets");
                Session mailSess = null;
                ChunkWriter store = new ChunkWriter() {
                    public void newChunk(int i, byte[] data) {
                        ByteBuffer bb = ByteBuffer.wrap(data);
                        session.write(bb);
                    }
                };
                ChunkingOutputStream out = new ChunkingOutputStream(store, 1024);
                m.getResource().writeTo(out);
                out.flush();
                popSession.reply(session, ".");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    public void dele(IoSession session, PopSession popSession, String[] args) {
        String sNum = args[1];
        int num = Integer.parseInt(sNum);
        Message mid = get(popSession, num);
        if (mid != null) {
            mid.markForDeletion();
            popSession.reply(session, "+OK");
            return;
        } else {
            popSession.reply(session, "-ERR no such message");
        }
    }
}
