package org.simple.sample.manager

import org.nutz.ioc.loader.annotation.IocBean
import org.simple.base.nutz.manager.CommonManager
import org.simple.base.nutz.model.Pair
import org.simple.sample.entity.StatusItem

/**
 * @author zhangjp
 */
@IocBean(fields = "dao")
class StatusManager extends CommonManager<StatusItem> {
    @Override
    Pair<String, String> getOperator(String userLoginId) {
        return null
    }
}
