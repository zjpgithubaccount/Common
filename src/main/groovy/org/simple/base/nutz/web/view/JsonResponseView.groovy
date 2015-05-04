package org.simple.base.nutz.web.view

import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.View
import org.simple.base.constants.BaseConstants
import org.simple.base.exception.AbstractException
import org.simple.base.json.model.JsonResponse
import org.simple.base.nutz.util.WebUtil
import org.simple.base.nutz.web.processor.JsonApiLogInfo

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonResponseView implements View {

    private static final Log log = Logs.getLog(JsonResponseView)

    public static final String VIEW = "jsonApi"

    Object obj

    JsonResponseView() {
    }

    JsonResponseView(Object obj) {
        this.obj = obj
    }

    @Override
    void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        Object jsonResponse
        obj = this.obj ?: obj

        def crossBack = req.getParameter("crossBack")

        switch (obj) {
            case AbstractException:
                jsonResponse = new JsonResponse(
                        errorCode: obj.code,
                        errorDescription: obj.message,
                        crossBack: crossBack
                ).toString()
                break

            case Throwable:
                jsonResponse = makeErrorJsonResponse(crossBack).toString()
                break

            case JsonResponse:
                obj.crossBack = crossBack
                jsonResponse = obj.toString()
                break

            default:
                jsonResponse = obj
        }

        req.setAttribute(JsonApiLogInfo.JSON_OUTPUT, WebUtil.renderJson(jsonResponse))
    }

    protected JsonResponse makeErrorJsonResponse(String crossBack) {
        new JsonResponse(
                errorCode: BaseConstants.SYSTEM_ERROR_CODE,
                errorDescription: BaseConstants.SYSTEM_ERROR_MESSAGE,
                crossBack: crossBack
        )
    }
}
