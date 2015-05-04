package org.simple.base.util

import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.upload.UploadOutOfSizeException
import org.nutz.mvc.upload.UploadUnsupportedFileNameException
import org.nutz.mvc.upload.UploadUnsupportedFileTypeException
import org.simple.cfg.api.SysCfg

/**
 * @author Jay.Wu
 */
class LogUtil {


    static String stackTrace(Throwable e) {
        StackTraceElement[] ste = e.getStackTrace()
        StringBuffer sb = new StringBuffer()
        sb.append(e.getMessage() + "\n")

        for (int i = 0; i < ste.length; i++) {
            sb.append(ste[i].toString() + "\n")
        }

        return sb.toString()
    }

    static String error2Mes(Throwable e) {
        if (e instanceof UploadUnsupportedFileTypeException
                || e instanceof UploadUnsupportedFileNameException) {
            return "系统不支持您上传的文件！"
        } else if (e instanceof UploadOutOfSizeException) {
            return "您上传的文件超出系统最大限制！"
        }

        return e.getMessage()
    }

    static long watch(LogWatch c) {
        watch {
            c.call()
        }
    }

    static long watch(Closure c) {
        long s = System.currentTimeMillis()
        c.call()
        long e = System.currentTimeMillis()
        return e - s
    }

    static void watch(Log log, String message, LogWatch c) {
        watch(log, message) {
            c.call()
        }
    }

    static void watch(Log log, String message, Closure c) {
        if (log.isDebugEnabled()) {
            long s = System.currentTimeMillis()
            c.call()
            long e = System.currentTimeMillis()
            long duration = e - s

            log.debugf(message, showSpendTime(duration))
        } else {
            c.call()
        }
    }

    /**
     * 检查是否开启日志
     *
     * @param cfgKey 配置文件key
     * @param clazz 需要检查的类
     * @param method 需要检查的方法
     * @param defaultEnable 默认值
     * @return 是否开启
     */
    static boolean isLogEnable(String cfgKey, String clazz, String method, boolean defaultEnable = true) {
        return eachActions(cfgKey, clazz, defaultEnable) { Map map ->
            if (map?.method?.include) {
                return map.method.include.any { method ==~ it }
            }

            if (map?.method?.exclude) {
                return !map.method.exclude.any { method ==~ it }
            }

            return defaultEnable
        }
    }

    /**
     * 获取指定的Log适配器
     *
     * @param cfgKey 配置文件key
     * @param clazz 需要检查的类
     * @param method 需要检查的方法
     * @return Log适配器
     */
    static Log getLog(String cfgKey, String clazz, String method) {
        return eachActions(cfgKey, clazz, null) { Map map ->
            Map logNode = map?.log

            if (logNode?.name) {
                Log log = Logs.getLog((String) logNode?.name)
                if (logNode?.include) {
                    return logNode.include.any { method ==~ it } ? log : null
                }

                if (logNode?.exclude) {
                    return logNode.exclude.any { method ==~ it } ? null : log
                }
            }

            return null
        }
    }

    /**
     * 循环所有需要处理的配置项
     */
    private static <T> T eachActions(String cfgKey, String clazz, T defaultValue, Closure<T> c) {
        if (SysCfg.get().getBoolean("${cfgKey}.open", false)) {
            List<Map> actions = []

            def value = SysCfg.getValue("${cfgKey}.actions")

            if (value instanceof List) {
                actions = value
            } else {
                actions << value
            }

            // 检查是否有开启条件
            actions = actions?.findAll {
                it.open
            }

            if (!actions) {
                return defaultValue
            }

            for (Map map : actions) {
                if (map.clazz ==~ clazz) {
                    return c(map)
                }
            }
        }

        return defaultValue
    }

    public static String showSpendTime(String name, Closure c) {
        return name + ", " + showSpendTime(watch(c))
    }

    public static String showSpendTime(Long millisecond) {
        Long second = millisecond / 1000
        Long minute = second / 60
        Long secAfterMin = second % 60
        return "Spend time " + millisecond + " ms ( " + minute + " m " + secAfterMin + " s )"
    }

    public static String spendTime(Long millisecond) {
        Long second = millisecond / 1000
        Long minute = second / 60
        Long secAfterMin = second % 60
        return millisecond + " ms ( " + minute + " m " + secAfterMin + " s )"
    }
}


interface LogWatch {
    void call()
}
