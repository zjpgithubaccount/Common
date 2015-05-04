package org.simple.base.util

import groovy.transform.CompileStatic

import java.lang.reflect.Array

/**
 * @author zhangjp 2015-04-28
 */
@CompileStatic
class ListUtil {

    /**
     * 判断collection是否为空
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty()
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0
    }

    /**
     * 判断数组是否不为空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects)
    }

    /**
     * 判断collection是否非空
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection)
    }

    /**
     * 返回第一个元素，若无则返回null
     */
    public static <T> T first(Collection<T> self) {
        if (isNotEmpty(self)) {
            return self.iterator().next()
        }

        return null
    }

    /**
     * 将数组转换为list
     */
    public static <T> List<T> array2List(T... array) {
        if (array != null) {
            return Arrays.asList(array)
        }
        return new ArrayList<T>()
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] list2Array(Collection<T> self) {
        if (isEmpty(self)) {
            return null
        }

        T[] result = (T[]) Array.newInstance(first(self).getClass(), self.size())
        return self.toArray(result)
    }
}
