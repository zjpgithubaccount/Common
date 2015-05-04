package org.simple.base.model

import com.alibaba.fastjson.annotation.JSONField
import org.nutz.json.JsonField
import org.simple.base.json.model.JsonResponse

class JsonApiResponse extends JsonResponse {

    // 基本属性 --//
    @JsonField('returnCode')
    @JSONField(name = 'returnCode')
    String errorCode = SUCCESS

    @JsonField('description')
    @JSONField(name = 'description')
    String errorDescription = '处理成功'
}