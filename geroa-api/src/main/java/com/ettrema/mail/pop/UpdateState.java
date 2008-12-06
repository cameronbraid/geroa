package com.ettrema.mail.pop;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateState implements PopState {

    private final static Logger log = LoggerFactory.getLogger(UpdateState.class);
    
    PopSession popSession;

    public UpdateState(PopSession popSession) {
        super();
        this.popSession = popSession;
        log.info("CREATED UpdateState");
    }

    public void enter(IoSession session, PopSession popSession) {
        for (MessageId mid : popSession.messageIds) {
            if (mid.deleted) {
                log.debug("deleting: " + mid.message.getId());
                mid.message.delete();
            }
        }
        popSession.reply(session, "+OK");
        session.close();
    }

    public void exit(IoSession session, PopSession popSession) {
    }
}
