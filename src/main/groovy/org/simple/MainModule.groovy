package org.simple

import org.nutz.mvc.annotation.Chain
import org.nutz.mvc.annotation.ChainBy
import org.nutz.mvc.annotation.Encoding
import org.nutz.mvc.annotation.Fail
import org.nutz.mvc.annotation.IocBy
import org.nutz.mvc.annotation.Localization
import org.nutz.mvc.annotation.Modules
import org.nutz.mvc.annotation.Ok
import org.nutz.mvc.annotation.SetupBy
import org.nutz.mvc.annotation.Views
import org.nutz.mvc.ioc.provider.ComboIocProvider
import org.simple.base.view.BizCustomizeMaker

@IocBy(type = ComboIocProvider.class, args = ["*org.nutz.ioc.loader.json.JsonLoader", "ioc/",
        "*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "org.simple"])
@Encoding(input = "utf8", output = "utf8")
@Modules(scanPackage = true)
@SetupBy(MvcSetup.class)
@ChainBy(args = ["ioc/chain.js"])
@Chain("mainChain")
@Localization("msg")
@Views(BizCustomizeMaker.class)
@Ok("ioc:json")
@Fail("json")
public class MainModule {
}