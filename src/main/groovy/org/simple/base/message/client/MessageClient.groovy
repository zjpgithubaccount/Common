package org.simple.base.message.client

import org.apache.mina.core.service.IoConnector
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.transport.socket.nio.NioSocketConnector
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.message.core.core.MessageContext
import org.simple.base.message.core.model.MessageResponse

/**
 * @author Jay Wu
 */
public class MessageClient {

    private static final Log log = Logs.getLog(MessageClient)

    private IoConnector connector;

    int port;

    String server;

    MessageClientHandler handler;

    boolean starting = false;

    MessageClient(String server, int port, MessageClientHandler handler) {
        this.server = server;
        this.port = port;
        this.handler = handler;
        init();
    }

    private void init() {
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);
        connector.getFilterChain().addLast("log", new LoggingFilter());
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        connector.setHandler(handler);
    }

    public void start(int checkInterval = 5) {
        try {
            connect();
            starting = true;
            check(checkInterval);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 心跳检测
     */
    public void check(int checkInterval) {
        Thread.start {
            while (starting) {
                try {
                    sleep(checkInterval * 1000);
                    def context = MessageContext.get(handler.id);
                    // 丢失服务器连接，尝试重连
                    if (context.getServerSession() == null
                            || !context.getServerSession().isConnected()) {
                        connect();
                    } else {
                        MessageResponse response = null;

                        try {
                            context.sendToServer(MessageContext.INTERNAL_CONNECT_CHECK)
                            response = context.getResponse(MessageContext.INTERNAL_CONNECT_CHECK);
                        } catch (Exception e) {
                            log.error(e.message);
                        }

                        if (!response) {
                            log.error('心跳检测失败:' + handler.ip(context.serverSession));
                            context.removeSession(MessageContext.SERVER);
                        }
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        };
    }

    private void connect() {
        log.infof("连接服务器%s:%s", server, port);
        connector.connect(new InetSocketAddress(server, port));
        // ConnectFuture future =
        // future.awaitUninterruptibly();
        // IoSession session = future.getSession();
        // session.write(i);
        // session.getCloseFuture().awaitUninterruptibly();
        // System.out.println("result=" + session.getAttribute("result"));
    }

    public void destroy() {
        starting = false;
        MessageContext.get(handler.id).getServerSession().close(true);
        MessageContext.get(handler.id).sessions.remove(MessageContext.SERVER);
        connector.dispose();
    }
}