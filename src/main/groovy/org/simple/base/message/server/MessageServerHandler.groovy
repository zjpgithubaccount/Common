package org.simple.base.message.server

import org.apache.mina.core.session.IoSession
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.message.core.core.MessageContext
import org.simple.base.message.core.core.MessageIoHandlerAdapter

/**
 * @author Jay Wu
 */
public abstract class MessageServerHandler extends MessageIoHandlerAdapter {

    private static final Log log = Logs.get();

    @Override
    public void sessionOpened(IoSession session) {
        String ip = ip(session);
        log.infof("接收来自客户端的连接 : %s, %s", ip, session.getId());
        MessageContext.get(id).getSessions().put(ip, session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String ip = ip(session);
        log.infof("接收来自客户端的关闭 : %s, %s", ip, session.getId());
        MessageContext.get(id).getSessions().remove(ip);
    }
}