package org.simple.base.message.core.model

import org.nutz.json.Json
import org.nutz.lang.random.R
import org.simple.base.serializable.SerializableHelper

/**
 *
 * @author Jay Wu
 *
 */
class MessageRequest<T> implements Serializable {

    /**
     * 唯一标识
     */
    String id = R.UU16();

    /**
     * 指令
     */
    String action;

    /**
     * 请求参数主体
     */
    T body;

    String toString() {
        return Json.toJson(this);
    }

    String serialize() {
        return SerializableHelper.toJSON(this)
    }
}
