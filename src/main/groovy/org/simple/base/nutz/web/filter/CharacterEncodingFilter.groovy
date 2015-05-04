package org.simple.base.nutz.web.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

public class CharacterEncodingFilter implements Filter {

    private String encoding

    private boolean forceEncoding = false

    private FilterConfig filterConfig

    // Handle the passed-in FilterConfig
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig
        this.encoding = filterConfig.getInitParameter("encoding")

        String forceEncodingStr = filterConfig.getInitParameter("forceEncoding")
        if (forceEncodingStr != null) {
            forceEncoding = Boolean.valueOf(forceEncodingStr)
        }
    }

    // Process the request/response pair
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
                request.setCharacterEncoding(this.encoding)
                if (this.forceEncoding) {
                    response.setCharacterEncoding(this.encoding)
                }
            }
            filterChain.doFilter(request, response)
        } catch (ServletException sx) {
            filterConfig.getServletContext().log(sx.getMessage())
        } catch (IOException iox) {
            filterConfig.getServletContext().log(iox.getMessage())
        }
    }

    // Clean up resources
    public void destroy() {
    }
}
