package org.simple.base.nutz.util

import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.nutz.model.Pair

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author zhangjp 2015-04-28
 */
class WebUtil {
    private static final Log log = Logs.getLog(WebUtil)

    private static def servletThreadLocal = new ThreadLocal<Pair<HttpServletRequest, HttpServletResponse>>()

    static HttpServletRequest getRequest() {
        return servletThreadLocal.get()?.first
    }

    static HttpServletResponse getResponse() {
        return servletThreadLocal.get()?.second
    }

    static void set(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        servletThreadLocal.set(new Pair(servletRequest, servletResponse))
    }

    static String params(String key) {
        return request.getParameter(key)
    }

    static Map<String, Object> requestAttr() {
        Map<String, Object> map = [:]

        for(String key in request.attributeNames) {
            map[key] = requestAttr(key)
        }

        return map
    }

    static <T> T requestAttr(String key) {
        return request.getAttribute(key) as T
    }

    static requestAttr(String key, Object value) {
        request.setAttribute(key, value)
    }

    static void removeRequestAttr(String key) {
        request.removeAttribute(key)
    }

    static <T> T sessionAttr(String key) {
        request.session.getAttribute(key) as T
    }

    static void sessionAttr(String key, Object value) {
        request.session.setAttribute(key, value)
    }

    static void removeSessionAttribute(String key) {
        request.session.removeAttribute(key)
    }

    static String transfer(String key) {
        def value = params(key)
        requestAttr(key, value)
        return value
    }

    static String renderJson(Object obj) {
        String json

        switch (obj) {
            case String:
                json = obj
                break
            default:
                json = obj?.toJson()
                break
        }

        response.setHeader("Cache-Control", "no-cache")
        response.contentType = "application/x-javascript;charset=UTF-8"

        try {
            response.writer.write(json ?: "")
            response.flushBuffer()
        } catch (Exception e) {
        }

        return json
    }

    static void renderHtml(String html) {
        response.setHeader("Cache-Control", "no-cache")
        response.contentType = "text/html;charset=UTF-8"
        response.writer.write(html)
        response.flushBuffer()
    }

    static Map<String, Object> params() {
        Map<String, Object> map = [:]
        for (String key in request.getParameterNames()) {
            map[key] = request.getParameter(key)
        }

        return map
    }

    static String getWebRoot() {
        return request.servletContext.getRealPath("/")
    }
}
