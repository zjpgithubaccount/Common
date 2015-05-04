package org.simple.base.view

import org.nutz.ioc.Ioc
import org.nutz.mvc.View
import org.simple.base.nutz.web.view.CustomizeMaker

class BizCustomizeMaker extends CustomizeMaker {

    @Override
    public View make(Ioc ioc, String type, String value) {
        switch (type) {
            case JsonApiResponseView.VIEW:
                return new JsonApiResponseView()
        }

        return super.make(ioc, type, value)
    }
}