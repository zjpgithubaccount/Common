package org.simple.base.message.core.exception

import org.simple.base.exception.ErrorCode

public enum SysCode implements ErrorCode {

    // 公共代码
    COMM_1000("1000", "请求消息体参数为空！"),
    COMM_1001("1001", "非法返回结果"),
    COMM_1002("系统出错，请联系管理员")

    SysCode(String code, String message) {
        this.code = code
        this.message = message
    }
}