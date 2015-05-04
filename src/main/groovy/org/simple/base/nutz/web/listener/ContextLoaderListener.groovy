package org.simple.base.nutz.web.listener

import org.simple.cfg.api.SysCfg
import org.simple.base.util.StringUtil

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

/**
 * <h3>Class name</h3>
 * <h4>Description</h4>
 * <p/>
 * <h4>Special Notes</h4>
 *
 * @author Jay 12-5-23 下午11:01
 * @version 1.0
 */
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String paths = servletContextEvent.getServletContext().getInitParameter("sysCfg")

        if (StringUtil.isNotBlank(paths)) {
            SysCfg.load(paths.split(",|\n"))
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
