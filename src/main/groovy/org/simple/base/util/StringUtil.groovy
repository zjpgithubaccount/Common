package org.simple.base.util

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.nutz.lang.Strings

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author zhangjp 2015-04-28
 */
@CompileStatic
class StringUtil extends  StringUtils {
    /**
     * 判断字符串是否为null或""
     *
     * @param str
     * @return
     */
    static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("")
    }

    /**
     * 判断字符串是否不为null或""
     *
     * @param str
     * @return
     */
    static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str)
    }

    /**
     * 首字母大写
     */
    public static String upperFirst(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase())
    }

    public static boolean isBlank(Object value) {
        if (value == null)
            return true

        if (value instanceof String) {
            if (Strings.isBlank((String) value))
                return true
        }

        return false
    }

    /**
     * 向右填充字符串：
     */
    public static String padRight(String self, int length) {
        StringBuilder result = new StringBuilder()

        for (int i = 0; i < length; i++) {
            result.append(self)
        }

        return result.toString()
    }

    public static String replace(String regex, String orgi, int pos) {
        if (isBlank(orgi) || isBlank(regex)) {
            return orgi;
        }

        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(orgi);

        while (m.find()) {
            String outStr = m.group(pos);
            orgi = orgi.replace(m.group(0), outStr);
        }

        return orgi;
    }

    /**
     * 根据正则表达式快速查找指定位置的字符串
     *
     * @param regex
     * @param orgi
     * @param pos
     * @return
     */
    public static String find(String regex, String orgi, int pos) {
        if (isBlank(orgi) || isBlank(regex)) {
            return null;
        }

        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(orgi.replaceAll("\r|\n", ""));

        while (m.find()) {
            String outStr = m.group(pos);
            return outStr;
        }

        return null;
    }
}
