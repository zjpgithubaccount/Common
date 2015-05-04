package org.simple.base.message.core.api

import org.simple.base.message.core.model.MessageResponse

public interface Callable<T> {
    void call(MessageResponse response);
}

