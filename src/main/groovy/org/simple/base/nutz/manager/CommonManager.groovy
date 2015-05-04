package org.simple.base.nutz.manager

import org.nutz.dao.Chain
import org.nutz.dao.Condition
import org.nutz.dao.pager.Pager
import org.nutz.service.IdNameEntityService
import org.simple.base.model.QuickMap
import org.simple.base.nutz.constants.NutzConstants
import org.simple.base.nutz.dao.CommonDao
import org.simple.base.nutz.entity.OperationEntity
import org.simple.base.nutz.model.Pair
import org.simple.base.util.StringUtil

import javax.servlet.http.HttpServletRequest

/**
 * @author Jay.Wu
 */
public abstract class CommonManager<T> extends IdNameEntityService<T> {

    @Override
    public CommonDao dao() {
        return (CommonDao) super.dao()
    }

    public T insert(T obj) {
        return dao().insert(obj)
    }

    public int update(T obj) {
        return dao().update(obj)
    }

    public int update(T obj, String active) {
        return dao().update(obj, active)
    }

    public T insert(T obj, String active) {
        return dao().insert(obj, active)
    }

    public int delete(T obj) {
        return dao().delete(obj)
    }

    /**
     * 自动判断insert还是update，支持集合，但集合内必须统一状态，只支持批量插入或者批量更新，不支持混合插入更新
     *
     * @param obj
     * @return
     */
    public T save(T obj) {
        return dao().save(obj)
    }

    /**
     * 自动判断insert还是update，支持集合，但集合内必须统一状态，只支持批量插入或者批量更新，不支持混合插入更新
     *
     * @param obj
     * @param active
     * @return
     */
    public T save(T obj, String active) {
        return dao().save(obj, active)
    }

    public Pager createPager(HttpServletRequest request, String pageName) {
        if (StringUtil.isBlank(pageName)) {
            pageName = NutzConstants.PAGE_NAME
        }

        String pageNumberStr = request.getParameter(pageName)
        String pageSizeStr = request.getParameter("pageSize")
        int pageSize = NutzConstants.PAGE_SIZE
        int pageNumber = 1

        if (StringUtil.isNotBlank(pageSizeStr)) {
            try {
                pageSize = Integer.valueOf(pageSizeStr)
            } catch (Exception e) {
            }
        }

        if (StringUtil.isNotBlank(pageNumberStr)) {
            try {
                pageNumber = Integer.valueOf(pageNumberStr)
            } catch (Exception e) {
            }
        }

        return dao().createPager(pageNumber, pageSize)
    }

    /**
     * 根据map创建Pager
     *
     * @param map
     * @return
     */
    public Pager createPager(QuickMap map) {
        String pageNumberStr = map.getString("pageNum")
        String pageSizeStr = map.getString("pageSize")
        int pageSize = NutzConstants.PAGE_SIZE
        int pageNumber = 1

        if (StringUtil.isNotBlank(pageSizeStr)) {
            try {
                pageSize = Integer.valueOf(pageSizeStr)
            } catch (Exception e) {
            }
        }

        if (StringUtil.isNotBlank(pageNumberStr)) {
            try {
                pageNumber = Integer.valueOf(pageNumberStr)
            } catch (Exception e) {
            }
        }

        return dao().createPager(pageNumber, pageSize)
    }

    public List<T> query(Condition cnd) {
        return query(cnd, null)
    }

    /**
     * 取前n条记录
     */
    public Pager limit(Integer n) {
        if (n == null) {
            return null
        }

        return dao().limit(n)
    }

    /**
     * 获取实体类对应的数据库表名
     */
    public String getTableName(Class<?> clazz) {
        return dao().getEntity(clazz).getTableName()
    }

    /**
     * 根据filter查找对象
     */
    T fetchWithFilter(Map<String, Object> filter) {
        return dao().fetchWithFilter(entityClass, filter)
    }

    /**
     * 根据filter过滤,返回对象集合
     */
    List<T> queryWithFilter(Map<String, Object> filter) {
        return dao().queryWithFilter(entityClass, filter)
    }

    /**
     * 初始化创建者，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> E initCreator(E obj, String userLoginId = null) {
        def operator = getOperator(userLoginId);
        obj.creator = operator.first;
        obj.creatorName = operator.second;
        obj.editor = obj.creator;
        obj.editorName = obj.creatorName;
        obj.createTime = obj.createTime ?: new Date();
        obj.updateTime = obj.updateTime ?: new Date();
        return obj;
    }

    /**
     * 初始化编辑者，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> E initEditor(E obj, String userLoginId = null) {
        def operator = getOperator(userLoginId);
        obj.editor = operator.first;
        obj.editorName = operator.second;
        obj.updateTime = new Date();
        return obj;
    }

    /**
     * 插入时追加创建者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> void insertWithOperator(E obj, String userLoginId = null, String active = null) {
        dao().insert(initCreator(obj, userLoginId), active ? active + "|createTime|creator|creatorName" : null);
    }

    /**
     * 插入时追加创建者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> void insertWithOperator(List<E> obj, String userLoginId = null, String active = null) {
        for (it in obj) {
            initCreator(it, userLoginId)
        }

        dao().insert(obj, active ? active + "|createTime|creator|creatorName" : null);
    }

    /**
     * 更新时追加编辑者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> int updateWithOperator(E obj, String userLoginId = null, String active = null) {
        return dao().update(initEditor(obj, userLoginId), active ? active + "|updateTime|editor|editorName" : null);
    }

    /**
     * 更新时追加编辑者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> int updateWithOperator(List<E> obj, String userLoginId = null, String active = null) {
        for (it in obj) {
            initEditor(it, userLoginId)
        }

        return dao().update(obj, active ? active + "|updateTime|editor|editorName" : null);
    }

    /**
     * 更新时忽略空值，追加编辑者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> int updateIgnoreNullWithOperator(E obj, String userLoginId = null) {
        return dao().updateIgnoreNull(initEditor(obj, userLoginId));
    }

    /**
     * 更新时追加编辑者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public <E extends OperationEntity> int updateWithOperator(Class<E> clazz, Chain chain, Condition cnd, String userLoginId = null) {
        def operator = getOperator(userLoginId);

        chain.add("updateTime", new Date())
                .add("editor", operator.first)
                .add("editorName", operator.second);

        return dao().update(clazz, chain, cnd);
    }

    /**
     * 更新时追加编辑者信息，当userLoginId为空时，会自动从session中获取当前登陆人信息
     */
    public int updateWithOperator(Chain chain, Condition cnd, String userLoginId = null) {
        return updateWithOperator(entityClass, chain, cnd, userLoginId);
    }

    abstract Pair<String, String> getOperator(String userLoginId)

    Pair<String, String> getOperator() {
        return getOperator(null)
    }
}
