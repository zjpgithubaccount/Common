var ioc = {
    mainChain: {
        ps: [
            "org.simple.base.nutz.web.processor.PropertyInitProcessor",
            "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
            "org.nutz.mvc.impl.processor.EncodingProcessor",
            "org.nutz.mvc.impl.processor.ModuleProcessor",
            "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
            "org.nutz.mvc.impl.processor.AdaptorProcessor",
            "org.simple.base.nutz.web.processor.MethodInvokeProcessor",
            "org.nutz.mvc.impl.processor.ViewProcessor"
        ],
        error: 'org.nutz.mvc.impl.processor.FailProcessor'
    }
};