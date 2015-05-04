package org.simple.base.exception

import groovy.transform.CompileStatic
import org.nutz.lang.Lang

/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
public abstract class AbstractException extends RuntimeException {
    String code

    AbstractException(String message) {
        super(message)
    }

    AbstractException(String message, String code) {
        super(message)
    }

    AbstractException(Throwable throwable) {
        super(throwable)
    }

    public static <T extends AbstractException> T me(Class<T> exceptionClass,
                           String code,
                           String message,
                           Object... objects) {
        if (objects) {
            message = String.format(message, objects)
        }

        T exception = Lang.makeThrow(exceptionClass, message)
        exception.code = code
        return exception
    }

    public static <T> T me(Class<T> exceptionClass,
                           ExceptionCode exceptionCode,
                           Object... objects) {
        return me(exceptionClass, exceptionCode?.code, exceptionCode?.value, objects)
    }
}

class ExceptionCode {
    String code

    String value
}

