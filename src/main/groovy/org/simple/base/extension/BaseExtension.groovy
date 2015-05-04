package org.simple.base.extension

import com.google.common.cache.Cache
import org.nutz.lang.Times
import org.simple.base.json.util.JsonUtil
import org.simple.base.util.CacheForm
import org.simple.base.util.CachesUtil
import org.simple.base.util.MapUtil

class BaseExtension {

    // ----------------------------------------------------------
    // Object
    static String toJson(Object self, boolean pretty = false) {
        JsonUtil.toJson(self, pretty)
    }

    static Map<String, Object> toMap(Object self) {
        MapUtil.toMap(self)
    }

    static Cache<Object, Object> withCache(Object self, Object key) {
        CachesUtil.withCache(self, key)
    }

    static Cache<Object, Object> withCache(Object self, Object key, String group, CacheForm form = null) {
        CachesUtil.withCache(self, key, group, form)
    }

    // ----------------------------------------------------------
    // String
    static Date toDate(String self, String format = null) {
        format ? Times.parse(format, self) : Times.D(self)
    }

    // ----------------------------------------------------------
    // Date
    static Date toDate(Date self, String format) {
        toDate(Times.format(format, self), format)
    }
}