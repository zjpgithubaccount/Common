package org.simple.base.nutz.web.tag

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.BodyTagSupport

class AuthTag extends BodyTagSupport {

    String readonlyClass = "readonly"

    String permissionContextKey = "permissions"

    String editPermissionAttrName = "edit"

    String viewPermissionAttrName = "view"

    @Override
    int doAfterBody() throws JspException {
        String content = bodyContent.string

        content = handlePermission((List<String>) pageContext.request.getAttribute(permissionContextKey), content)

        bodyContent.getEnclosingWriter().write(content)
        return super.doEndTag()
    }

    private List<String> getRequiredPermissions(Element element, String attrName) {
        def permissionStr = element.attr(attrName)
        return permissionStr ? permissionStr?.split(',')*.trim()?.toList() : null
    }

    private String handlePermission(List<String> operatorPermissions, String content) {
        def doc = Jsoup.parse(content)
        for (it in doc.select("*[$editPermissionAttrName],*[$viewPermissionAttrName]")) {
            List<String> requiredEditPermissions = getRequiredPermissions(it, editPermissionAttrName)
            List<String> requiredViewPermissions = getRequiredPermissions(it, viewPermissionAttrName)

            boolean view = true

            if (requiredViewPermissions) {
                if (!operatorPermissions) {
                    it.remove()
                    view = false
                } else {
                    if (operatorPermissions.disjoint(requiredViewPermissions)) {
                        it.remove()
                        view = false
                    }
                }
            }

            if (view && requiredEditPermissions) {
                if (!operatorPermissions) {
                    it.addClass(readonlyClass)
                } else {
                    if (operatorPermissions.disjoint(requiredEditPermissions)) {
                        it.addClass(readonlyClass)
                    }
                }
            }
        }

        return doc.body().html()
    }
}
