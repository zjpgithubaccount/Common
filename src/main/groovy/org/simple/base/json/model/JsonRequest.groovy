package org.simple.base.json.model

import groovy.transform.CompileStatic
import org.simple.base.json.util.JsonUtil

/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
public class JsonRequest<T> {
    /**
     * 客户端类型
     */
    String client

    /**
     * 客户端版本
     */
    String version

    /**
     * session标识
     */
    String sessionId

    /**
     * 请求参数主体
     */
    T body

    public String toString() {
        JsonUtil.toJson(this)
    }
}