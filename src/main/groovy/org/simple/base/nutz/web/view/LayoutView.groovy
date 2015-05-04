package org.simple.base.nutz.web.view

import com.google.common.cache.CacheLoader
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.nutz.lang.Files
import org.nutz.lang.Lang
import org.nutz.lang.Strings
import org.nutz.mvc.Mvcs
import org.nutz.mvc.view.JspView
import org.simple.cfg.api.SysCfg
import org.simple.base.nutz.util.HtmlUtil
import org.simple.base.util.CachesUtil

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class LayoutView extends JspView {

    public static final String VIEW = 'jsp'

    static final String CACHE_KEY = "LayoutView"

    public static final String LAYOUT = "layout"

    public LayoutView(String name) {
        super(name)
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Exception {
        String path = evalPath(req, obj)

        if (path == null) {
            resp.getWriter().write("Path为空，请检查Action配置")
            return
        }

        String args = ""
        if (path.contains("?")) { //将参数部分分解出来
            args = path.substring(path.indexOf('?'))
            path = path.substring(0, path.indexOf('?'))
        }

        String ext = getExt()
        // 空路径，采用默认规则
        if (Strings.isBlank(path)) {
            path = Mvcs.getRequestPath(req)
            path = "/WEB-INF"
            +(path.startsWith("/") ? "" : "/")
            +Files.renameSuffix(path, ext)
        }
        // 绝对路径 : 以 '/' 开头的路径不增加 '/WEB-INF'
        else if (path.charAt(0) == '/') {
            if (!path.toLowerCase().endsWith(ext))
                path += ext
        }
        // 包名形式的路径
        else {
            path = "/WEB-INF/" + path.replace('.', '/') + ext
        }

        path = path + args

        // 处理布局模板
        Map<String, String> layoutConf = parseLayoutConfWithCache(req.getSession().getServletContext().getRealPath(path))

        if (!layoutConf.isEmpty()) {
            String content = HtmlUtil.getJspContent(req, resp, path)
            parseLayoutAttr(req, content, layoutConf)
            path = SysCfg.get().getString("layout.template") + layoutConf.get(LAYOUT)
        }

        // 执行 Forward
        RequestDispatcher rd = req.getRequestDispatcher(path)

        if (rd == null)
            throw Lang.makeThrow("Fail to find Forward '%s'", path)
        // Do rendering
        rd.forward(req, resp)
    }

    /**
     * 解析模板内的变量
     */
    private void parseLayoutAttr(HttpServletRequest req,
                                 String content, Map<String, String> layoutConf) {
        Document doc = null
        layoutConf.each { k, v ->
            if (k != LAYOUT) {
                doc = doc ?: Jsoup.parse(content)
                req.setAttribute(k, doc.select(v)?.first()?.html())
            }
        }
    }

    /**
     * 解析jsp文件内的配置
     */
    private Map<String, String> parseLayoutConf(String path) {
        Map<String, String> map = [:]

        new File(path).text.eachLine {
            if (it.contains("<c:set")) {
                def matcher = it =~ "<c:set\\s+?var=\"(.+)\"\\s+?value=\"(.+)\"\\s*?/?>"

                String var = matcher[0][1]
                if (var.startsWith(LAYOUT)) {
                    String value = matcher[0][2]

                    if (var == LAYOUT) {
                        map.put(var, value)
                    } else {
                        map.put(var - LAYOUT - "_", value)
                    }
                }
            }

            return true
        }

        return map
    }

    /**
     * 解析jsp文件内的配置，开启缓存
     */
    private Map<String, String> parseLayoutConfWithCache(String path) {
        if (SysCfg.get().getBoolean("layout.cache", false)) {
            return (Map<String, String>) CachesUtil.getOrCreateCache(CACHE_KEY, { String key ->
                parseLayoutConf(key)
            } as CacheLoader<Object, Object>, null).get(path)
        } else {
            return parseLayoutConf(path)
        }
    }
}