package org.simple.base.util

import org.nutz.lang.FailToGetValueException
import org.nutz.lang.Mirror
import org.nutz.log.Log
import org.nutz.log.Logs

import java.lang.reflect.Field
import java.lang.reflect.Modifier

public class ClassUtil {

    public static final Log log = Logs.get()

    /**
     * 在两个不同的对象中，只copy相同名称的属性，不复制null值
     *
     * @param dest
     * @param orig
     * @author Jay.Wu
     */
    public static void copySameProperties(Object dest, Object orig) {
        Mirror<?> dMirror = Mirror.me(dest)
        Mirror<?> oMirror = Mirror.me(orig)

        for (Field field : dMirror.getFields()) {
            field.setAccessible(true)
            String filedName = field.getName()

            int modify = field.getModifiers()
            if (Modifier.isFinal(modify) || Modifier.isTransient(modify)) {
                continue
            }

            try {
                Object value = oMirror.getValue(orig, filedName)

                if (value != null) {
                    dMirror.setValue(dest, filedName, value)
                }
            } catch (FailToGetValueException e) {
            }
        }
    }

    public static Class<?> currentClass() {
        try {
            return Class.forName(Thread.currentThread().getStackTrace()[2].getClassName())
        } catch (ClassNotFoundException e) {
            return null
        }
    }

    public static String getClassPath() {
        return getClassPath(ClassUtil.class)
    }

    public static String getClassPath(Class clazz) {
        String separator = "/"
        String path = clazz.getResource("").getPath()

        String selfPath = clazz.getPackage().getName().replace(".", separator)
        path = path.replace(separator + selfPath, "")

        if (isWindows() && path.indexOf(separator) == 0) {
            path = path.substring(1)
        }

        return path
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1
    }

    public static String getBasePath() {
        return getBasePath(ClassUtil.class)
    }

    public static String getBasePath(Class clazz) {
        return getPath(clazz, 1)
    }

    public static String getWebRoot() {
        return getWebRoot(ClassUtil.class)
    }

    public static String getWebRoot(Class clazz) {
        return getPath(clazz, 2)
    }

    public static String getPath(int subPos) {
        return getPath(ClassUtil.class, subPos)
    }

    public static String getPath(Class clazz, int subPos) {
        try {
            String separator = "/"
            String path = getClassPath(clazz)
            String[] paths = path.split(separator)
            String[] newPaths = new String[paths.length - subPos]
            System.arraycopy(paths, 0, newPaths, 0, newPaths.length)

            return StringUtil.join(newPaths, separator) + separator
        } catch (Exception e) {
            log.error(e.getMessage(), e)
            return null
        }
    }

    public static String convertPath(String path) {
        return path.replace('$root', getWebRoot())
    }

    static String getValue(Object obj, Field field) {
        Mirror.me(obj.class).getValue(obj, field)
    }

    static String getValue(Object obj, String fieldName) {
        Mirror.me(obj.class).getValue(obj, fieldName)
    }
}
