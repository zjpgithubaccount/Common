package org.simple.base.exception

import groovy.transform.CompileStatic


/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
trait ErrorCode {
    String message

    String code

    /**
     * 格式化异常信息然后抛出
     *
     * @param fmt
     */
    void error(Object... fmt) {
        throw new ServiceException(fmt ? String.format(message, fmt) : message, code);
    }

    /**
     * 自定义异常信息然后抛出
     *
     * @param message
     */
    void error(String message) {
        throw new ServiceException(message, code)
    }

    /**
     * 断言条件为真，否则抛出格式化后的异常信息
     *
     * @param cnd
     * @param fmt
     */
    void assertTrue(boolean cnd, Object... fmt) {
        if (!cnd) {
            error(fmt)
        }
    }

    /**
     * 断言条件为真，否则抛出自定义异常信息
     *
     * @param cnd
     * @param message
     */
    void assertTrue(boolean cnd, String message) {
        if (!cnd) {
            error(message)
        }
    }

    /**
     * 断言条件为假，否则抛出格式化后的异常信息
     *
     * @param cnd
     * @param fmt
     */
    void assertFalse(boolean cnd, Object... fmt) {
        assertTrue(!cnd, fmt)
    }

    /**
     * 断言条件为假，否则抛出自定义异常信息
     *
     * @param cnd
     * @param message
     */
    void assertFalse(boolean cnd, String message) {
        assertTrue(!cnd, message)
    }

    /**
     * 断言条件不为空，否则抛出格式化后的异常信息
     *
     * @param cnd
     * @param fmt
     */
    void assertNotEmpty(Object cnd, Object... fmt) {
        if (!cnd) {
            error(fmt)
        }
    }

    /**
     * 断言条件不为空，否则抛出自定义异常信息
     *
     * @param cnd
     * @param message
     */
    void assertNotEmpty(Object cnd, String message) {
        if (!cnd) {
            error(message)
        }
    }
}