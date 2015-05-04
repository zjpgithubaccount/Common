package org.simple.base.api

import org.nutz.dao.pager.Pager
import org.simple.base.model.JsonApiResponse
import org.simple.base.nutz.action.CommonAction

/**
 * Created by jaywu on 15/2/3.
 */
class BaseApi extends CommonAction {
    protected JsonApiResponse ok(Pager pager, List rows) {
        return new JsonApiResponse(
                body: [
                        rows : rows ?: [],
                        pager: pager
                ]
        )
    }

    protected JsonApiResponse ok(Object body = null, String description = null) {
        def response = new JsonApiResponse();

        if (body) {
            response.body = body
        }
        if (description) {
            response.errorDescription = description
        }

        return response
    }
}
