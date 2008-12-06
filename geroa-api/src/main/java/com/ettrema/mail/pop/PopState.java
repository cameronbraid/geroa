package com.ettrema.mail.pop;

import org.apache.mina.common.IoSession;

/**
 *
 * @author brad
 */
public interface PopState {
        void enter(IoSession session, PopSession popSession);

        void exit(IoSession session, PopSession popSession);
}
