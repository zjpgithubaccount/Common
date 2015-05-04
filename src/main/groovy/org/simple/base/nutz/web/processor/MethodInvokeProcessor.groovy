package org.simple.base.nutz.web.processor

import org.nutz.aop.ClassAgent
import org.nutz.lang.Lang
import org.nutz.lang.Stopwatch
import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.ActionContext
import org.nutz.mvc.impl.processor.AbstractProcessor
import org.simple.base.json.annotation.JsonApi
import org.simple.base.nutz.util.WebUtil
import org.simple.base.util.LogUtil

import java.lang.reflect.InvocationTargetException

class MethodInvokeProcessor extends AbstractProcessor {

    private static Log log = Logs.getLog(MethodInvokeProcessor)

    @Override
    void process(ActionContext ac) throws Throwable {
        def method = ac.getMethod()
        def path = getClassPath(ac)

        def logInfo = new JsonApiLogInfo(
                log: log,
                action: path,
                batchNo: System.nanoTime().toString(),
                paramMap: WebUtil.params(),
                method: method.name,
                module: getClassName(ac),
                enable: getLogEnable(ac),
                request: ac.getRequest().getAttribute(JsonApiLogInfo.JSON_INPUT)
        )

        try {
            logWhenAccess(logInfo)

            methodInvoke(ac)

            logInfo.response = ac.getRequest().getAttribute(JsonApiLogInfo.JSON_OUTPUT)
            logWhenDone(logInfo)
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw Lang.unwrapThrow(e)
        } catch (InvocationTargetException e) {
            throw e.getCause()
        }
    }

    protected boolean getLogEnable(ActionContext ac) {
        ac.module.getClass().getAnnotation(JsonApi) != null
    }

    protected void methodInvoke(ActionContext ac) {
        ac.setMethodReturn(ac.method.invoke(ac.module, ac.methodArgs))
        doNext(ac)
    }

    /**
     * 访问前打印日志
     */
    protected def logWhenAccess(JsonApiLogInfo logInfo) {
        if (!logInfo.enable) {
            return
        }

        logInfo.start()


        logInfo.log.infof("%s, batchNo:%s\nRequest:\n%s\n",
                logInfo.action,
                logInfo.batchNo,
                logInfo.request ?: logInfo.paramMap?.toJson())
    }

    /**
     * 完成后打印日志
     */
    protected def logWhenDone(JsonApiLogInfo logInfo) {
        if (!logInfo.enable) {
            return
        }

        logInfo.stop()

        if (LogUtil.isLogEnable(JsonApiLogInfo.LOG_CFG, logInfo.module, logInfo.method, true)) {
            logInfo.log.infof("%s, batchNo:%s, %s\nResponse:\n%s",
                    logInfo.action,
                    logInfo.batchNo,
                    LogUtil.showSpendTime(logInfo.spendTime),
                    logInfo.response)
        } else {
            logInfo.log.infof("%s, batchNo:%s, %s",
                    logInfo.action,
                    logInfo.batchNo,
                    LogUtil.showSpendTime(logInfo.spendTime))
        }
    }

    String getClassPath(ActionContext ac) {
        getClassName(ac) + "." + ac.getMethod().getName()
    }

    String getClassName(ActionContext ac) {
        ac.getModule().getClass().getName().replace(ClassAgent.CLASSNAME_SUFFIX, "")
    }
}

class JsonApiLogInfo {

    final static String LOG_CFG = "log4Json"

    final static String JSON_INPUT = "JSON_INPUT"

    final static String JSON_OUTPUT = "JSON_OUTPUT"

    Log log

    String batchNo

    Map<String, Object> paramMap

    String request

    String response

    String module

    String method

    String action

    Boolean enable = false

    private def sw = Stopwatch.create()

    void start() {
        sw.start()
    }

    void stop() {
        sw.stop()
    }

    Long getSpendTime() {
        sw.getDuration()
    }
}