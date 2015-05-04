package org.simple.base.message.core.core

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession
import org.nutz.lang.random.R
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.exception.ServiceException
import org.simple.base.message.core.exception.SysCode
import org.simple.base.message.core.model.MessageRequest
import org.simple.base.message.core.model.MessageResponse
import org.simple.base.serializable.SerializableHelper

public abstract class MessageIoHandlerAdapter extends IoHandlerAdapter {

    private static final Log log = Logs.get();

    public final String id = R.UU16();

    public String ip(IoSession session) {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        return remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();
    }

    public void messageReceived(IoSession session, Object message) {
        if (message == null) {
            return;
        }

        if (message.equals(MessageContext.INTERNAL_CONNECT_CHECK)) {
            MessageResponse response = new MessageResponse();
            response.setId(MessageContext.INTERNAL_CONNECT_CHECK);
            session.write(response);
            return;
        }

        if (message instanceof String) {
            message = SerializableHelper.fromJSON((String) message);
        }

        if (message instanceof MessageRequest) {
            MessageResponse response;

            try {
                response = doReceived(session, (MessageRequest) message);
            } catch (ServiceException e) {
                response = new MessageResponse();
                response.setCode(e.getCode());
                response.setMessage(e.getMessage());
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                response = new MessageResponse();
                response.setCode(SysCode.COMM_1002.code);
                response.setMessage(SysCode.COMM_1002.message);
            }

            if (response != null) {
                session.write(response);
            }
        } else if (message instanceof MessageResponse) {
            // 放入消息池中
            MessageResponse m = (MessageResponse) message;
            MessageContext.get(id).putResponse(m.getId(), m);
        } else {
            SysCode.COMM_1001.error()
        }
    }

    protected abstract MessageResponse doReceived(IoSession session, MessageRequest message);

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }
}
