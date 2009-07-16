package com.ettrema.mail.pop;

import com.ettrema.mail.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.StreamWriteFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaPopServer implements PopServer {

    private final static Logger log = LoggerFactory.getLogger(MinaPopServer.class);

    private IoAcceptor acceptor;
    private int popPort;
    MailResourceFactory resourceFactory;
    final List<Filter> filters;

    public MinaPopServer(MailResourceFactory resourceFactory, List<Filter> filters) {
        this(110, resourceFactory, filters);
    }

    public MinaPopServer(int popPort, MailResourceFactory resourceFactory, List<Filter> filters) {
        this.popPort = popPort;
        this.resourceFactory = resourceFactory;
        this.filters = filters;
    }

    public MinaPopServer(int popPort, MailResourceFactory resourceFactory, Filter filter) {
        this( popPort, resourceFactory, Arrays.asList( filter));
    }

    
    
    public void start() {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();

        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
//        cfg.getFilterChain().addLast("mimemessage1", new MimeMessageIOFilter() );
        cfg.getFilterChain().addLast("logger", new LoggingFilter());        
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("US-ASCII"))));
        cfg.getFilterChain().addLast("stream", new StreamWriteFilter() );
        try {
            //cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            acceptor.bind(new InetSocketAddress(popPort), new PopIOHandlerAdapter(this), cfg);
        } catch (IOException ex) {
            throw new RuntimeException("Couldnt bind to port: " + popPort, ex);
        }

    }

    public void stop() {
        acceptor.unbindAll();
        acceptor = null;
    }

    public int getPopPort() {
        return popPort;
    }

    public void setPopPort(int popPort) {
        this.popPort = popPort;
    }

    public MailResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    public void setResourceFactory(MailResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    
    
    static PopSession sess(IoSession session) {
        return (PopSession) session.getAttribute("stateMachine");
    }

}
