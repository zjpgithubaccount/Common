package org.simple.base.message.core.model

import org.nutz.json.Json
import org.simple.base.serializable.SerializableHelper

/**
 *
 * @author Jay Wu
 *
 */
public class MessageResponse<T> implements Serializable {

    public static final String SUCCESS = "0";

    String id;

    String code = SUCCESS;

    String message;

    T body;

    String toString() {
        return Json.toJson(this);
    }

    String serialize() {
        return SerializableHelper.toJSON(this)
    }
}
