package org.simple.base.nutz.web.view

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

public class WrapperResponse extends HttpServletResponseWrapper {

    StringWriter stringWriter
    PrintWriter printWriter

    public WrapperResponse(HttpServletResponse res) {
        super(res)
        stringWriter = new StringWriter()
        printWriter = new PrintWriter(stringWriter)
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter
    }

    public String getContent() {
        return stringWriter.toString()
    }
}