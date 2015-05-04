package org.simple.base.nutz.web.view

import org.nutz.ioc.Ioc
import org.nutz.mvc.View
import org.nutz.mvc.ViewMaker

public class CustomizeMaker implements ViewMaker {

    @Override
    public View make(Ioc ioc, String type, String value) {
        switch (type) {
            case LayoutView.VIEW:
                return new LayoutView(value)

            case JsonResponseView.VIEW:
                return new JsonResponseView()
        }

        return null
    }
}

