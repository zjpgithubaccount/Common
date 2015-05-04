package org.simple.base.nutz.util

import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.nutz.web.view.WrapperResponse
import org.simple.base.util.LogUtil
import org.simple.base.util.StringUtil

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Jay.Wu
 */
public class HtmlUtil {

    public static final Log log = Logs.get();

    public static String appendBackslash(String str) {

        // http://www.vladstudio.com/wallpaper/?242/1280x1024/low
        // http://127.0.0.1/file/123.html?path="http://jdo.008.net/"
        // 去除？后面带的参数的情况（如：id=1），但“?234”之类的不去除
        str = str.replaceAll("\\?\\w+=.*\$", "");

        // 127.0.0.1
        if (str.indexOf("http://") == -1)
            str = "http://" + str;

        if (str.charAt(str.length() - 1) != '/') {
            String regex = "http://.+/";
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(str);
            String temp = null;
            while (m.find())
                temp = m.group(0);
            // http://127.0.0.1
            if (temp == null)
                str += "/";
            else {
                temp = null;
                // http://127.0.0.1/file/123.html"
                regex = "(http://.+/)\\w+\\.\\w+\$";
                p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                m = p.matcher(str);
                while (m.find()) {
                    temp = m.group(1);
                }
                // http://www.vladstudio.com/wallpaper/?242/1280x1024/low
                if (temp == null)
                    str += "/";
                else
                    str = temp;
            }
        }

        return str;
    }

    public static String subUrl(String url, int slash) {
        String outUrl = url.substring(7);
        int slashIndex = 0;
        for (int i = 0; i <= slash; i++) {
            slashIndex = outUrl.indexOf('/', slashIndex);
        }
        outUrl = outUrl.substring(0, slashIndex + 1);
        return "http://" + outUrl;
    }

    /**
     * 取出网址中的文件名
     *
     * @param str 网址
     * @return 文件名
     */
    public static String getURLfileName(String str) {
        // 127.0.0.1
        if (str.indexOf("http://") == -1)
            str = "http://" + str;

        String outStr = null;
        // http://127.0.0.1/file/123.html?id=1
        String regex = "http://.+/(.*\\..+\$)";
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str);
        while (m.find()) {
            outStr = m.group(1).replaceAll("\\?.*", "");
        }

        return outStr;
    }

    public static String getJspContent(HttpServletRequest req, HttpServletResponse resp, String path) {
        WrapperResponse rep = new WrapperResponse(resp);
        try {
            req.getRequestDispatcher(path).include(req, rep);
            return rep.getContent();
        } catch (Exception e) {
            log.error(LogUtil.stackTrace(e), e);
        }

        return null;
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        def cookies = request.getCookies();
        if (cookies == null || name == null || name.length() == 0) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (name.equals(cookies[i].getName())) {
                return cookies[i];
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie c = getCookie(request, name);
        if (c != null) {
            return c.getValue();
        }
        return null;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        deleteCookie(request, response, getCookie(request, name));
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
            cookie.setPath(getPath(request));
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name,
                                 String value) {
        setCookie(request, response, name, value, 365 * 24 * 60 * 60);
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name,
                                 String value, Integer maxAge) {
        Cookie cookie = new Cookie(name, value == null ? "" : value);

        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        cookie.setPath(getPath(request));
        response.addCookie(cookie);
    }

    public static String getPath(HttpServletRequest request) {
        String path = request.getContextPath();
        return (path == null || path.length() == 0) ? "/" : path;
    }

    public static String getRuningPath() {
        try {
            String path = StringUtil.class.getResource("").getPath();

            // if (path.indexOf("/") == 0) {
            // path = path.substring(1);
            // }

            if (path.indexOf("WEB-INF") > -1) {
                path = path.substring(0, path.indexOf("WEB-INF"));
            }

            if (path.indexOf("bin") > -1) {
                path = path.substring(0, path.indexOf("bin"));
            }

            return path;
        } catch (Exception e) {
        }
        return null;
    }
}
