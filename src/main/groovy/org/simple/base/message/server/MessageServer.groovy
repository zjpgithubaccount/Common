package org.simple.base.message.server

import org.apache.mina.core.service.IoAcceptor
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.message.core.core.MessageContext
import org.simple.base.message.core.model.MessageResponse

/**
 * @author Jay Wu
 */
public class MessageServer {

    private static final Log log = Logs.getLog(MessageServer)

    private IoAcceptor acceptor;

    MessageServerHandler handler;

    int port;

    MessageServer(MessageServerHandler handler, int port) {
        this.handler = handler;
        this.port = port;
    }

    public void start() {
        try {
            acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast("log", new LoggingFilter());
            acceptor.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
            acceptor.setHandler(handler);
            acceptor.setReuseAddress(true);
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.bind(new InetSocketAddress(port));

            log.info("Listening port " + port);

//            check(10);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 心跳检测
     */
    public void check(int checkInterval) {
        Thread.start {
            while (true) {
                try {
                    sleep(checkInterval * 1000);
                    def context = MessageContext.get(handler.id);

                    List<String> sessions = [];

                    // 尝试清理无效session
                    for (entry in context.sessions) {
                        if (entry.value.isConnected()) {
                            MessageResponse response = null;
                            try {
                                context.send(entry.value, MessageContext.INTERNAL_CONNECT_CHECK);
                                response = context.getResponse(MessageContext.INTERNAL_CONNECT_CHECK);
                            } catch (Exception e) {
                                log.error(e.message);
                            }

                            if (!response) {
                                sessions << entry.key;
                            }
                        } else {
                            sessions << entry.key;
                        }
                    }

                    for (key in sessions) {
                        log.error('心跳检测失败: ' + key);
                        context.removeSession(key);
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        };
    }

    public void destroy() {
        for (String key : MessageContext.get(handler.id).sessions.keySet()) {
            try {
                IoSession session = MessageContext.get(handler.id).sessions.get(key);
                session.close(true);
            } catch (Exception e) {
                log.error(e.message);
            }

            MessageContext.get(handler.id).sessions.remove(key);
        }

        acceptor.unbind();
        acceptor.dispose();
    }
}