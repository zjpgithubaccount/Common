package org.simple.base.util

import groovy.transform.CompileStatic

/**
 * @author zhangjp 2015-04-28
 */
@CompileStatic
class AssertUtil {
    /**
     * 抛出提示异常
     */
    public static <T extends Throwable> void error(T exception) throws T {
        throw exception
    }

    /**
     * 抛出提示异常
     */
    public static <T extends Throwable> void error(String message, Class<T> exception) throws T {
        // 使用指定构造函数
        throw exception.newInstance(message)
    }

    /**
     * 判断object是否为空
     */
    public static <T extends Throwable> void notNull(Object object, String message, Class<T> exception)
            throws T {
        if (object == null) {
            error(message, exception)
        }
    }

    /**
     * 判断object是否为空
     */
    public static <T extends Throwable> void notNull(Object object, T exception) throws T {
        if (object == null) {
            error(exception)
        }
    }

    /**
     * 判断是否为假，否则抛出提示异常
     */
    public static <T extends Throwable> void assertFalse(Boolean b, T exception) throws T {
        if (b != null && b) {
            error(exception)
        }
    }

    /**
     * 判断是否为真，否则抛出提示异常
     */
    public static <T extends Throwable> void assertTrue(Boolean b, T exception) throws T {
        assertFalse(!b, exception)
    }

    /**
     * 判断collection是否为空
     */
    public static <T extends Throwable> void notEmpty(Collection<?> collection, String message,
                                                      Class<T> exception) throws T {
        if (ListUtil.isEmpty(collection)) {
            error(message, exception)
        }
    }

    /**
     * 判断collection是否为空
     */
    public static <T extends Throwable> void notEmpty(Collection<?> collection, T exception) throws T {
        if (ListUtil.isEmpty(collection)) {
            error(exception)
        }
    }
}
