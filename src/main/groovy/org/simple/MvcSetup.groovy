package org.simple

import org.apache.log4j.PropertyConfigurator
import org.nutz.ioc.Ioc
import org.nutz.lang.Files
import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.mvc.NutConfig
import org.nutz.mvc.Setup
import org.simple.base.nutz.util.IocUtil

public class MvcSetup implements Setup {

    public static final Log log = Logs.get();

    public void init(NutConfig config) {
        try {
            PropertyConfigurator.configureAndWatch(Files.findFile('log4j.properties').absolutePath, 1000)

            Ioc ioc = config.getIoc();

            log.info("初始化IocUtil");

            IocUtil.setContext(config.getServletContext());
            IocUtil.setIoc(ioc);

            IocUtil.monitorFiles();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void destroy(NutConfig config) {
    }
}
