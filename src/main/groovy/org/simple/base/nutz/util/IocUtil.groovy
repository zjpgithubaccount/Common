package org.simple.base.nutz.util

import org.apache.log4j.PropertyConfigurator
import org.nutz.dao.impl.FileSqlManager
import org.nutz.ioc.Ioc
import org.nutz.ioc.impl.NutIoc
import org.nutz.ioc.loader.combo.ComboIocLoader
import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.Mvcs
import org.simple.base.util.FilesPlus
import org.simple.base.nutz.dao.CommonDao
import org.simple.base.util.StringUtil

import javax.servlet.ServletContext

public class IocUtil {

    public static final Log log = Logs.get()

    public static Ioc ioc

    private static ServletContext context

    public static Ioc getIoc() {
        if (ioc == null) {
            if (context == null) {
                try {
                    ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "ioc/",
                            "*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "com.ac"))
                } catch (ClassNotFoundException e) {
                    log.error(e.message, e)
                }
            } else {
                ioc = Mvcs.getIoc()
            }
        }

        return ioc
    }

    /**
     * 取出bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T) getIoc().get(null, name)
    }

    /**
     * 取出bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getIfExist(String name) {
        return (T) (getIoc().has(name) ? getIoc().get(null, name) : null)
    }

    /**
     * 取出bean
     */
    public static <T> T get(Class<T> type, String name) {
        return getIoc().get(type, name)
    }

    /**
     * 取出bean
     */
    public static <T> T get(Class<T> clazz) {
        return ioc.get(clazz)
    }

    /**
     * 监控配置文件
     */
    public static void monitorFiles() {
        monitorFiles("sql")
    }

    /**
     * 监控配置文件
     */
    public static void monitorFiles(String sqlPath) {
        // 处理sqls文件
        if (StringUtil.isBlank(sqlPath)) {
            sqlPath = "sql"
        }

        final FileSqlManager fileSqlManager = new FileSqlManager(sqlPath)
        fileSqlManager.refresh()
        dao().setSqlManager(fileSqlManager)

        FilesPlus.addListener(sqlPath, new FilesPlus.FileListener() {
            @Override
            public void changed(File target) {
                fileSqlManager.refresh()
            }
        })

        String logPath = FilesPlus.findFile("log4j.properties").getAbsolutePath()
        PropertyConfigurator.configureAndWatch(logPath, 1000)
    }

    public static CommonDao dao() {
        return ioc.get(CommonDao, 'dao')
    }

    public static ServletContext getContext() {
        return context
    }

    public static void setContext(ServletContext context) {
        this.context = context
    }

    public static void setIoc(Ioc ioc) {
        this.ioc = ioc
    }
}