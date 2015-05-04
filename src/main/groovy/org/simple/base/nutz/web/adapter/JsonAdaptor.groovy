package org.simple.base.nutz.web.adapter

import org.nutz.lang.Lang
import org.nutz.mvc.adaptor.PairAdaptor
import org.nutz.mvc.adaptor.ParamInjector
import org.nutz.mvc.annotation.Param
import org.simple.base.json.util.JsonUtil
import org.simple.base.nutz.web.processor.JsonApiLogInfo
import org.simple.base.util.StreamsPlus

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Type

class JsonAdaptor extends PairAdaptor {
    @Override
    protected ParamInjector evalInjector(Type type, Param param) {
        if (param == null) {
            return new ParamInjector() {
                @Override
                Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
                    req.setAttribute(JsonApiLogInfo.JSON_INPUT, refer)
                    return JsonUtil.fromJson(type, (String) refer)
                }
            }
        } else {
            return super.evalInjector(type, param)
        }
    }

    @Override
    protected Object getReferObject(ServletContext sc, HttpServletRequest req, HttpServletResponse resp,
                                    String[] pathArgs) {
        try {
            return StreamsPlus.read(StreamsPlus.utf8r(req.getInputStream())).toString()
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e)
        }
    }
}
