package org.simple.base.nutz.web.processor

import org.nutz.mvc.ActionContext
import org.nutz.mvc.impl.processor.AbstractProcessor
import org.simple.base.nutz.util.WebUtil

public class PropertyInitProcessor extends AbstractProcessor {

    private static Boolean x = null

    public void process(ActionContext ac) throws Throwable {
        WebUtil.set(ac.getRequest(), ac.getResponse())
        doNext(ac)
    }
}