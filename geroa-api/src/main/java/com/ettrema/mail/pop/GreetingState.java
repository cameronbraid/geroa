package com.ettrema.mail.pop;

import org.apache.mina.common.IoSession;

public class GreetingState implements PopState {

    PopSession popSession;

    public GreetingState(PopSession popSession) {
        super();
        this.popSession = popSession;
    }

    public void enter(IoSession session, PopSession popSession) {
        popSession.reply(session, "+OK POP3 server ready <1896.697170952@dbc.mtview.ca.us>"); //todo
        popSession.transitionTo(session, new AuthState(popSession));
    }

    public void exit(IoSession session, PopSession popSession) {
    }

    public void messageReceived(IoSession session, Object msg, PopSession popSession) {
    }
}
