package org.simple.base.message.core.core

import org.apache.mina.core.session.IoSession
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.message.core.api.Callable
import org.simple.base.message.core.model.MessageResponse

class MessageContext {

    private final static Map<String, MessageContext> cache = [:];

    private static Log log = Logs.getLog(MessageContext);

    Map<String, IoSession> sessions = new HashMap<String, IoSession>();

    Map<String, MessageResponse> pool = [:];

    public static final String SERVER = "server";

    public static final String INTERNAL_CONNECT_CHECK = "INTERNAL_CONNECT_CHECK";

    private MessageContext() {}

    static MessageContext get(String key) {
        return cache.get(key, new MessageContext());
    }

    void removeSession(String key) {
        log.info('移除session:' + key)
        sessions.remove(key);
    }

    IoSession getServerSession() {
        return sessions.get(SERVER);
    }

    void sendToServer(Object request) {
        getServerSession().write(request);
    }

    void send(IoSession session, Object request) {
        session.write(request);
    }

    void sendToAll(Object request) {
        for (IoSession session : sessions.values()) {
            send(session, request);
        }
    }

    void putResponse(String key, MessageResponse response) {
        pool.put(key, response);
    }

    void getResponse(String id, Callable callable, Integer timeout = 5000) {
        def start = System.currentTimeMillis();

        while (true) {
            def response = pool.get(id);

            if (response) {
                pool.remove(id);
                callable(response);
                return;
            }

            if (timeout) {
                def end = System.currentTimeMillis();
                if (end - start > timeout) {
                    log.warn("Message Timeout:$id");
                    return;
                }
            }

            Thread.sleep(10);
        }
    }

    MessageResponse getResponse(String id, Integer timeout = 5000) {
        MessageResponse result = null;

        getResponse(id, new Callable() {
            @Override
            void call(MessageResponse response) {
                result = response;
            }
        }, timeout);

        return result;
    }

    void asynchronousGetResponse(String id, Callable callable, Integer timeout = 5000) {
        new Thread() {
            void run() {
                getResponse(id, callable, timeout);
            }
        }
    }
}
