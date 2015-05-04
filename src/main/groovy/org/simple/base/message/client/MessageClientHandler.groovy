package org.simple.base.message.client

import org.apache.mina.core.session.IoSession
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.message.core.core.MessageContext
import org.simple.base.message.core.core.MessageIoHandlerAdapter

/**
 * 客户端处理器
 *
 * @author Jay Wu
 */
public abstract class MessageClientHandler extends MessageIoHandlerAdapter {

    private static final Log log = Logs.get();

    @Override
    public void sessionOpened(IoSession session) {
        String ip = ip(session);
        log.infof("连接服务器端成功 : %s, %s", ip, session.getId());
        MessageContext.get(id).getSessions().put(MessageContext.SERVER, session);
    }
}