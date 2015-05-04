package org.simple.base.exception

import groovy.transform.CompileStatic
import org.simple.base.nutz.model.Pair

/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
class ServiceException extends AbstractException {

    ServiceException(String message) {
        super(message)
    }

    ServiceException(String message, String code) {
        super(message)
        this.code = code
    }

    ServiceException(Throwable throwable) {
        super(throwable)
    }

    public static ServiceException me(String code,
                                      String message,
                                      Object... objects) {
        return me(ServiceException, code, message, objects)
    }

    public static ServiceException me(Pair<String, String> pair,
                                      Object... objects) {
        return me(ServiceException, pair.first, pair.second, objects)
    }

    public static ServiceException me(ExceptionCode exceptionCode,
                                      Object... objects) {
        return me(ServiceException, exceptionCode, objects)
    }
}

