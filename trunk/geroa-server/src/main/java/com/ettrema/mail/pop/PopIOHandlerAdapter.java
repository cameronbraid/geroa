package com.ettrema.mail.pop;

import com.ettrema.mail.Event;
import com.ettrema.mail.Filter;
import com.ettrema.mail.FilterChain;
import com.ettrema.mail.PopMessageEvent;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.TransportType;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopIOHandlerAdapter extends IoHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(PopIOHandlerAdapter.class);

    MinaPopServer server;

    public PopIOHandlerAdapter(MinaPopServer server) {
        super();
        this.server = server;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable t) throws Exception {
        t.printStackTrace();
        session.close();
    }

    @Override
    public void messageReceived(final IoSession session, final Object msg) throws Exception {
        log.info("pop message: " + msg);
        PopMessageEvent event = new PopMessageEvent(session, msg);
        Filter terminal = new Filter() {

            public void doEvent(FilterChain chain, Event event) {
                MinaPopServer.sess(session).messageReceived(session, msg);
            }
        };
        FilterChain chain = new FilterChain(server.filters, terminal);
        chain.doEvent(event);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.info("Session created...");
        if (session.getTransportType() == TransportType.SOCKET) {
            ((SocketSessionConfig) session.getConfig()).setReceiveBufferSize(2048);
        }
        session.setIdleTime(IdleStatus.BOTH_IDLE, 10);
        PopSession sess = new PopSession(session, server.resourceFactory);
        session.setAttribute("stateMachine", sess);
    }
}
