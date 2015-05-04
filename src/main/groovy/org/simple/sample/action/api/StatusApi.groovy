package org.simple.sample.action.api

import groovy.transform.CompileStatic
import org.nutz.dao.Chain
import org.nutz.dao.pager.Pager
import org.nutz.ioc.loader.annotation.Inject
import org.nutz.ioc.loader.annotation.IocBean
import org.nutz.mvc.annotation.At
import org.nutz.mvc.annotation.Fail
import org.nutz.mvc.annotation.Ok
import org.simple.base.api.BaseApi
import org.simple.base.json.annotation.JsonApi
import org.simple.base.model.JsonApiResponse
import org.simple.base.model.QuickMap
import org.simple.base.nutz.dao.CndPlus
import org.simple.base.nutz.util.IocUtil
import org.simple.query.util.QueryUtil
import org.simple.sample.entity.StatusItem
import org.simple.sample.manager.StatusManager
import org.simple.base.view.JsonApiResponseView

/**
 * 增删查改操作Demo
 *
 * @author zhangjp
 */
@At("/status/api")
@IocBean
@JsonApi
@Ok(JsonApiResponseView.VIEW)
@Fail(JsonApiResponseView.VIEW)
@CompileStatic
class StatusApi extends BaseApi {
    @Inject
    StatusManager statusManager

    /**
     * query方法查询
     * @return
     */
    @At
    JsonApiResponse getStatusList() {
        List<StatusItem> statusItemList = statusManager.query(CndPlus.where("1", "=", "1"))
        return ok(null, statusItemList)
    }

    /**
     *  query方法分页查询
     * @return
     */
    @At
    JsonApiResponse getStatusList1() {
        Pager pager = new Pager()
        pager.pageNumber = 1
        pager.pageSize = 5

        List<StatusItem> statusItemList = statusManager.query(CndPlus.where("1", "=", "1"), pager);

        return ok(pager, statusItemList)
    }

    /**
     *  queryWithFilter方法查询
     * @return
     */
    @At
    JsonApiResponse getStatusList2() {
        QuickMap filter = ["statusTypeId": "ORDER_STATUS"]
        List<StatusItem> statusItemList = statusManager.queryWithFilter(filter)

        return ok(statusItemList)
    }

    /**
     * queryWithCount方法查询
     * @return
     */
    @At
    JsonApiResponse getStatusList3() {
        QuickMap filter = [statusTypeId: "ORDER_STATUS"]
        List<StatusItem> statusItemList = IocUtil.dao().queryWithCount(
                QueryUtil.createByKey("getStatusItem").putAll(filter),
                StatusItem)

        return ok(statusItemList)
    }

    /**
     * statusManager的fetch方法获取
     */
    @At
    JsonApiResponse getStatusItem() {
        StatusItem statusItem = statusManager.fetch("ORDER_ITEM_COMPLETED")
        return ok(statusItem)
    }

    /**
     * IocUtil.dao()的fetch方法获取，区别在于要传入StatusItem.class
     * @return
     */
    @At
    JsonApiResponse getStatusItem1() {
        StatusItem statusItem = IocUtil.dao().fetch(StatusItem, "ORDER_ITEM_PAID")
        return ok(statusItem)
    }

    /**
     * 全部字段更新
     * @return
     */
    @At
    JsonApiResponse updateStatusItem() {
        StatusItem statusItem = IocUtil.dao().fetch(StatusItem, "ORDER_ITEM_PAID")
        statusItem.updateTime = new Date()

        int count = statusManager.update(statusItem)

        return ok(count)
    }

    /**
     * 只更新updateTime字段
     * @return
     */
    @At
    JsonApiResponse updateStatusItem1() {
        statusManager.update(Chain.make("updateTime", new Date()), CndPlus.where("id", "ORDER_CANCELLED"))

        return ok()
    }

    /**
     * 只更新creatorName|creator字段
     * @return
     */
    @At
    JsonApiResponse updateStatusItem2() {
        StatusItem statusItem = IocUtil.dao().fetch(StatusItem, "ORDER_ITEM_PAID")
        statusItem.creator = "zjp"
        statusItem.creatorName = "zhangjp"
        statusItem.sequenceId = "11111"

        int count = statusManager.update(statusItem, "creatorName|creator")

        return ok(count)
    }

    /**
     * 更新非空字段
     * @return
     */
    @At
    JsonApiResponse updateStatusItem3() {
        StatusItem statusItem = new StatusItem()
        statusItem.id = "ORDER_ITEM_PAID"
        statusItem.creator = "creator"

        int count = IocUtil.dao().updateIgnoreNull(statusItem)

        return ok(count)
    }

    /**
     * 批量更新
     * @return
     */
    @At
    JsonApiResponse updateStatusItem4() {
        List<StatusItem> statusItemList = statusManager.query(
                CndPlus.where("id", "TRAFFIC_COMPLETED").or("id", "TRAFFIC_IN_PROCESS"))
        statusItemList.each {
            it.updateTime = new Date()
        }

        int count = IocUtil.dao().update(statusItemList)

        return ok(count)
    }

    /**
     * 全部字段插入
     * @return
     */
    @At
    JsonApiResponse insertStatusItem() {
        StatusItem statusItem = new StatusItem(
                id: "statusItem1",
                statusTypeId: "ORDER_STATUS",
                statusCode: "CANCELLED",
                description: "sample"
        )

        StatusItem statusItem1 = statusManager.insert(statusItem)

        return ok(statusItem1)
    }

    /**
     * 部分字段插入
     * @return
     */
    @At
    JsonApiResponse insertStatusItem1() {
        StatusItem statusItem = new StatusItem(
                id: "statusItem2",
                statusTypeId: "ORDER_STATUS",
                statusCode: "COMPLETED",
                description: "sample"
        )

        StatusItem statusItem1 = statusManager.insert(statusItem, "id|statusTypeId|statusCode")

        return ok(statusItem1)
    }

    /**
     * 批量插入
     * @return
     */
    @At
    JsonApiResponse insertStatusItem2() {
        List<StatusItem> statusItemList = new ArrayList<StatusItem>()
        StatusItem statusItem1 = new StatusItem(
                id: "statusItem3",
                statusTypeId: "ORDER_STATUS",
                statusCode: "COMPLETED",
                description: "sample"
        )
        StatusItem statusItem2 = new StatusItem(
                id: "statusItem4",
                statusTypeId: "ORDER_STATUS",
                statusCode: "COMPLETED",
                description: "sample"
        )
        statusItemList.add(statusItem1)
        statusItemList.add(statusItem2)

        List<StatusItem> statusItems = IocUtil.dao().insert(statusItemList)

        return ok(statusItems)
    }

    /**
     * 根据ID删除
     * @return
     */
    @At
    JsonApiResponse deleteStatusItem() {
        int count = IocUtil.dao().delete(StatusItem, "statusItem1")

        return ok(count)
    }

    /**
     * 批量删除
     * @return
     */
    @At
    JsonApiResponse deleteStatusItem1() {
        List<StatusItem> statusItemList = new ArrayList<StatusItem>()
        StatusItem statusItem1 = new StatusItem(
                id: "statusItem3",
                statusTypeId: "ORDER_STATUS",
                statusCode: "COMPLETED",
                description: "sample"
        )
        StatusItem statusItem2 = new StatusItem(
                id: "statusItem4",
                statusTypeId: "ORDER_STATUS",
                statusCode: "COMPLETED",
                description: "sample"
        )
        statusItemList.add(statusItem1)
        statusItemList.add(statusItem2)

        int count = IocUtil.dao().delete(statusItemList)

        return ok(count)
    }
}
