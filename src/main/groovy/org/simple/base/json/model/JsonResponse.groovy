package org.simple.base.json.model

import com.alibaba.fastjson.annotation.JSONField
import groovy.transform.CompileStatic
import org.nutz.json.JsonField
import org.nutz.lang.Strings
import org.simple.base.json.util.JsonUtil

/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
public class JsonResponse<T> {

    public static final String SUCCESS = "0"

    JsonResponse() {}

    JsonResponse(String errorCode, String errorDescription, T body, boolean compact) {
        this.errorCode = errorCode
        this.errorDescription = errorDescription
        this.body = body
        this.compact = compact
    }

    // 基本属性 --//
    @JsonField('error_code')
    @JSONField(name = 'error_code')
    String errorCode = SUCCESS

    @JsonField('error_description')
    @JSONField(name = 'error_description')
    String errorDescription = '处理成功'

    T body

    /**
     * 跨域请求
     */
    @JsonField(ignore = true)
    @JSONField(serialize = false)
    String crossBack

    /**
     * 是否压缩json
     */
    @JsonField(ignore = true)
    @JSONField(serialize = false)
    transient boolean compact = true

    public String toString() {
        String json = JsonUtil.toJson(this, !compact)

        if (!Strings.isBlank(crossBack)) {
            json = crossBack + "(" + json + ")"
        }

        return json
    }
}
