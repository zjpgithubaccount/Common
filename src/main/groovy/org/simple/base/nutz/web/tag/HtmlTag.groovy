package org.simple.base.nutz.web.tag

import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.cfg.api.SysCfg
import org.simple.base.util.FilesPlus
import org.simple.base.model.QuickMap
import org.simple.base.util.GroovyUtil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.servlet.jsp.JspException
import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.tagext.BodyTagSupport
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier

public class HtmlTag extends BodyTagSupport {
    private static final long serialVersionUID = 0L

    private static final Log log = Logs.get()

    private Map map = new HashMap()

    public void setAttribute(String paramString1, String paramString2) {
        this.map.put(paramString1, paramString2)
    }

    public String getAttribute(String paramString) {
        return (String) this.map.get(paramString)
    }

    public int getAttributeCount() {
        return this.map.size()
    }

    public String getHtmlAttribute() {
        StringBuffer localStringBuffer = new StringBuffer(" ")
        Iterator localIterator = this.map.keySet().iterator()
        while (localIterator.hasNext()) {
            String str = localIterator.next().toString()
            localStringBuffer.append(str).append("=").append("\"").append(this.map.get(str)).append("\" ")
        }
        return localStringBuffer.toString()
    }

    public void release() {
        this.map.clear()
        super.release()
    }

    public Map getAttributeMap() {
        return this.map
    }

    public HttpServletRequest request() {
        return (HttpServletRequest) this.pageContext.getRequest()
    }

    public HttpServletResponse response() {
        return (HttpServletResponse) this.pageContext.getResponse()
    }

    public HttpSession session() {
        return request().getSession()
    }

    public JspWriter out() {
        return this.pageContext.getOut()
    }

    public Writer getOutput() {
        return this.pageContext.getOut()
    }

    public JspWriter println(Object paramObject) throws JspException {
        JspWriter localJspWriter = (JspWriter) getOutput()
        try {
            localJspWriter.println(paramObject)
        } catch (IOException e) {
            log.error(e)
        }
        return localJspWriter
    }

    public JspWriter print(Object paramObject) throws JspException {
        JspWriter localJspWriter = (JspWriter) getOutput()

        try {
            localJspWriter.print(paramObject)
        } catch (IOException e) {
            log.error(e)
        }

        return localJspWriter
    }

    protected JspWriter printInput(String paramString1, String paramString2, String paramString3)
            throws JspException {
        return println("<input type='" + paramString1 + "' name='" + paramString2 + "' id='" + paramString2
                + "' value='" + paramString3 + "' >")
    }

    protected JspWriter printTd(String paramString) throws JspException {
        return println("<td>" + paramString + "</td>")
    }

    protected Object getSource(String paramString) {
        if (paramString != null) {
            Object localObject = request().getAttribute(paramString)
            if (localObject == null)
                localObject = this.pageContext.getAttribute(paramString)
            if (localObject == null)
                localObject = request().getParameter(paramString)
            if (localObject == null)
                localObject = request().getSession().getAttribute(paramString)
            return localObject
        }

        return null
    }

    protected String getValue(String paramString1, String paramString2) {
        if (paramString1 == null)
            return ""
        String str = null
        if (paramString2 != null) {
            Object localObject1 = request().getAttribute(paramString2)
            if (localObject1 == null)
                localObject1 = this.pageContext.getAttribute(paramString2)
            if (localObject1 == null)
                localObject1 = request().getParameter(paramString2)
            if (localObject1 == null)
                localObject1 = request().getSession().getAttribute(paramString2)
            if (localObject1 != null) {
                Object localObject2
                if ((localObject1 instanceof Map)) {
                    localObject2 = (Map) localObject1
                    Object localObject3 = ((Map) localObject2).get(paramString1)
                    str = localObject3 == null ? "" : localObject3.toString()
                } else {
                    try {
                        localObject2 = PropertyUtils.getNestedProperty(localObject1, paramString1)
                        str = localObject2 == null ? "" : localObject2.toString()
                    } catch (IllegalAccessException localIllegalAccessException) {
                        localIllegalAccessException.printStackTrace()
                    } catch (InvocationTargetException localInvocationTargetException) {
                        localInvocationTargetException.printStackTrace()
                    } catch (NoSuchMethodException localNoSuchMethodException) {
                        localNoSuchMethodException.printStackTrace()
                    }
                }
            }
        } else {
            str = paramString1
        }
        if (str == null)
            str = ""
        return (String) (String) str
    }

    protected void cleanField() throws Exception {
        Field[] arrayOfField1 = getClass().getDeclaredFields()

        for (Field localField : arrayOfField1) {
            localField.setAccessible(true)
            int m = localField.getModifiers()

            if (Modifier.isFinal(m)
                    || Modifier.isStatic(m)) {
                continue
            }

            if (!localField.getType().isPrimitive()) {
                localField.set(this, null)
            } else if (Boolean.TYPE == localField.getType()) {
                localField.set(this, false)
            } else {
                if ((!localField.getType().getClass().equals(Number.class))
                        && (!localField.getType().getClass().getSuperclass().equals(Number.class)))
                    continue
                localField.set(this, 0)
            }
        }
    }

    protected void process(String key, QuickMap map) throws Exception {
        String path = "tag/" + key + "." + SysCfg.GS

        Binding b = new Binding()
        b.setVariable("out", new StringBuilder())

        for (String k : map.keySet()) {
            b.setVariable(k, map.get(k))
        }

        GroovyUtil.parseScriptText(path, FilesPlus.read(path), b)

        Writer writer = getOutput()
        writer.append(b.getVariable("out").toString())
    }
}
