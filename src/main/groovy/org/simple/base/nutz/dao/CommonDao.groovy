package org.simple.base.nutz.dao

import org.nutz.castor.Castors
import org.nutz.dao.Condition
import org.nutz.dao.FieldFilter
import org.nutz.dao.SqlManager
import org.nutz.dao.Sqls
import org.nutz.dao.entity.Entity
import org.nutz.dao.entity.Record
import org.nutz.dao.entity.annotation.Table
import org.nutz.dao.impl.NutDao
import org.nutz.dao.impl.sql.callback.QueryRecordCallback
import org.nutz.dao.pager.Pager
import org.nutz.dao.pager.ResultSetLooping
import org.nutz.dao.sql.Sql
import org.nutz.dao.sql.SqlCallback
import org.nutz.dao.sql.SqlContext
import org.nutz.lang.Each
import org.nutz.lang.Lang
import org.nutz.lang.Strings
import org.nutz.log.Log
import org.nutz.log.Logs
import org.nutz.resource.Scans
import org.nutz.trans.Molecule
import org.simple.cfg.api.SysCfg
import org.simple.base.model.QuickMap
import org.simple.base.nutz.entity.CommonEntity
import org.simple.base.nutz.model.Pair
import org.simple.query.util.QueryUtil
import org.simple.base.util.ListUtil
import org.simple.base.util.MapUtil
import org.simple.base.util.StringUtil

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Jay.Wu
 */
public class CommonDao extends NutDao {

    private static final Log log = Logs.get()

    public static final String CACHE_ENTITY = "CACHE_ENTITY_"

    public static final String CACHE_ENTITY_LIST = "CACHE_ENTITY_LIST_"

    public static final Pair<String, String> GET_CACHE_ENTITY_ERROR = new Pair("GET_CACHE_ENTITY_ERROR", "获取缓存失败")

    public static final Long CACHE_ENTITY_MAX_SIZE = SysCfg.get().getLong("cache.entity.fetchMaxSize", 20)

    public static final Long CACHE_ENTITY_MAX_LIFE = SysCfg.get().getLong("cache.entity.fetchMaxLife", 30 * 60)

    /* ========================================================== */

    public CommonDao() {
        super()
    }

    public CommonDao(DataSource dataSource) {
        super(dataSource)
    }

    public CommonDao(DataSource dataSource, SqlManager sqlManager) {
        super(dataSource, sqlManager)
    }

    public DataSource getDataSource() {
        return dataSource
    }

    /**
     * 获取指定key的sql语句
     */
    public String getSql(String key) {
        return sqls().get(key)
    }

    /**
     * 若有分页对象，将会自动计算出总数
     */
    public List<Record> queryWithCount(QueryUtil queryUtil) {
        return innerQuery(queryUtil, true, Record.class)
    }

    /**
     * 若有分页对象，不会自动计算出总数
     */
    public List<Record> queryWithoutCount(QueryUtil queryUtil) {
        return innerQuery(queryUtil, false, Record.class)
    }

    /**
     * 若有分页对象，将会自动计算出总数
     */
    public <T> List<T> queryWithCount(QueryUtil queryUtil, Class<T> classOfT) {
        return innerQuery(queryUtil, true, classOfT)
    }

    /**
     * 若有分页对象，不会自动计算出总数
     */
    public <T> List<T> queryWithoutCount(QueryUtil queryUtil, Class<T> classOfT) {
        return innerQuery(queryUtil, false, classOfT)
    }

    private <T> List<T> innerQuery(QueryUtil queryUtil, Boolean needCount, Class<T> classOfT) {
        if (queryUtil == null) {
            return null
        }

        initQueryUtil(queryUtil)
        String queryString = queryUtil.getQueryString()
        Pager pager = queryUtil.getPager()

        if (pager != null) {
            if (needCount) {
                pager.setRecordCount(count(queryUtil))
            }

            Sql sql = Sqls.create(queryString)
            sql.setPager(pager)
            getJdbcExpert().formatQuery(sql)
            queryUtil.setQueryString(sql.getSourceSql())
        }

        return innerQuery(queryUtil, classOfT)
    }

    public <T> List<T> queryWithFilter(Class<T> classOfT, Map<String, ?> filter) {
        return queryWithFilter(classOfT, filter, null)
    }

    public <T> List<T> queryWithFilter(Class<T> classOfT, Map<String, ?> filter, Pager pager) {
        return query(classOfT, CndPlus.putAll(filter), pager)
    }

    public int execute(QueryUtil queryUtil) {
        initQueryUtil(queryUtil)
        queryUtil.setConvert(false)
        queryUtil.process()
        Sql sql = Sqls.create(queryUtil.getQueryString())
        queryParam2SqlParam(queryUtil, sql)

        expert.formatQuery(sql)
        return _exec(sql)
    }

    public int count(QueryUtil q) {
        QueryUtil cq = q.clone()

        initQueryUtil(cq)
        String newQueryString = "SELECT COUNT(*) FROM (" + cq.getQueryString() + ") auto"
        cq.setQueryString(newQueryString)
        cq.setQueryKey(null)
        cq.putAll(q.params())

        return queryForInt(cq)
    }

    public QuickMap fetchWithQueryUtil(QueryUtil queryUtil) {
        List<QuickMap> list = innerQuery(queryUtil, false, QuickMap.class)

        if (ListUtil.isNotEmpty(list)) {
            return list.get(0)
        }

        return null
    }

    /**
     * 若filter为空，则返回null
     */
    public <T> T fetchWithFilter(Class<T> classOfT, Map<String, ?> filter) {
        if (MapUtil.isEmptyValue(filter)) {
            return null
        }

        return fetch(classOfT, CndPlus.putAll(filter))
    }

    public Record fetchRecord(QueryUtil queryUtil) {
        List<Record> list = innerQuery(queryUtil, false, Record.class)

        if (ListUtil.isNotEmpty(list)) {
            return list.get(0)
        }

        return null
    }

    public <T> T fetch(QueryUtil queryUtil, Class<T> classOfT) {
        initQueryUtil(queryUtil)
        List<T> list = queryWithoutCount(queryUtil, classOfT)

        if (ListUtil.isNotEmpty(list)) {
            return list.get(0)
        }

        return null
    }

    public int queryForInt(QueryUtil queryUtil) {
        initQueryUtil(queryUtil)
        List<Record> list = innerQuery(queryUtil, Record.class)

        if (ListUtil.isNotEmpty(list)) {
            for (Object v : list.get(0).values()) {
                return Integer.valueOf(v.toString())
            }
        }

        return 0
    }

    public int queryForSum(QueryUtil queryUtil) {
        initQueryUtil(queryUtil)
        List<Record> list = innerQuery(queryUtil, Record.class)
        return list?.sum() ?: 0
    }

    private void initQueryUtil(QueryUtil queryUtil) {
        if (StringUtil.isNotBlank(queryUtil.getQueryKey())) {
            queryUtil.setQueryString(getSql(queryUtil.getQueryKey()).replace(";", ""))
        }
    }


    private static Class[] recordClass = [
            Record.class, QuickMap.class, String.class, BigDecimal.class,
            Integer.class, Double.class, Date.class, Long.class, Float.class]

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> innerQuery(QueryUtil queryUtil, Class<T> classOfT) {
        if (queryUtil == null) {
            return null
        }

        queryUtil.setConvert(false)
        String queryString

        queryUtil.process()
        queryString = queryUtil.getQueryString()

        Sql sql

        boolean isRecord = false

        for (Class c : recordClass) {
            if (classOfT == c) {
                isRecord = true
                break
            }
        }
        if (!isRecord) {
            Entity<T> entity = getEntity(classOfT)
            sql = Sqls.queryEntity(queryString).setEntity(entity)
        } else {
            sql = Sqls.create(queryString)
            if (classOfT == QuickMap.class) {
                sql.setCallback(new SqlCallback() {
                    @Override
                    public Object invoke(Connection conn, ResultSet rs, Sql s) throws SQLException {
                        ResultSetLooping ing = new ResultSetLooping() {
                            protected boolean createObject(int index, ResultSet r2, SqlContext context, int rowCout) {
                                list.add(QuickMap.create(r2))
                                return true
                            }
                        }
                        ing.doLoop(rs, s.getContext())
                        return ing.getList()
                    }
                })
            } else {
                sql.setCallback(new QueryRecordCallback())
            }
        }

        queryParam2SqlParam(queryUtil, sql)

        log.debug("queryString:\n" + queryString + "\nvalues: " + queryUtil.getParamValues())
        execute(sql)
        List<T> list = sql.getList(classOfT)

        if (ListUtil.isNotEmpty(list)) {
            if (isRecord) {
                if (classOfT == QuickMap.class) {
                } else if (classOfT != Record.class) {
                    List<T> newList = new ArrayList<T>()

                    for (T map : list) {
                        Record r = (Record) map
                        Object v = r.values().iterator().next()

                        T value


                        if (v == null) {
                            value = (T) v
                        } else if (!v.getClass().equals(classOfT)) {
                            value = Castors.me().castTo(v, classOfT)
                        } else {
                            value = (T) v
                        }

                        newList.add(value)
                    }

                    list = newList
                }
            }
        }

        return list
    }

    private void queryParam2SqlParam(QueryUtil queryUtil, Sql sql) {
        for (String key : queryUtil.params().keySet()) {
            Object value = queryUtil.getParam(key)

            if (key.startsWith('$')) {
                sql.vars().set(key.substring(1), value)
            }

            sql.params().set(key, value)
        }
    }

    public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
        List<T> list = super.query(classOfT, condition, pager)

        if (pager != null) {
            pager.setRecordCount(count(classOfT, condition))
        }

        return list
    }

    public <T> T insert(final T obj, String active) {
        Object first = Lang.first(obj)

        if (null == first)
            return obj

        if (Strings.isBlank(active))
            return insert(obj)

        Molecule m = new Molecule() {
            public void run() {
                setObj(insert(obj))
            }
        }

        FieldFilter.create(first.getClass(), active).run(m)
        return m.getObj() as T
    }

    @Override
    public <T> T insert(T obj) {
        initCreateTime(obj)
        return super.insert(obj)
    }

    private void initCreateTime(Object obj) {
        if (obj instanceof CommonEntity) {
            CommonEntity entity = (CommonEntity) obj
            if (entity.getCreateTime() == null) {
                entity.setCreateTime(new Date())
            }

            if (entity.getUpdateTime() == null) {
                entity.setUpdateTime(entity.getCreateTime())
            }
        }
    }

    /**
     * 自动判断insert还是update，支持集合，但集合内必须统一状态，只支持批量插入或者批量更新，不支持混合插入更新
     */
    public <T extends CommonEntity> T save(T obj) {
        return save(obj, null)
    }

    /**
     * 自动判断insert还是update，支持集合，但集合内必须统一状态，只支持批量插入或者批量更新，不支持混合插入更新
     */
    public <T extends CommonEntity> T save(T obj, String actived) {
        if (Lang.length(obj) > 0) {
            Object first = Lang.first(obj)
            if (first.hasProperty('id')) {
                obj = insert(obj, actived)
            } else {
                update(obj, actived)
            }
        }

        return obj
    }

    /**
     * 批量删除，支持集合
     */
    public int delete(Object obj) {
        if (null == obj)
            return -1

        final int[] re = new int[1]
        if (Lang.length(obj) > 0) {
            Lang.each(obj, new Each<Object>() {
                public void invoke(int i, Object ele, int length) {
                    re[0] = re[0] + singleDelete(ele)
                }
            })
        }

        return re[0]
    }

    /**
     * 单个对象删除
     */
    public int singleDelete(Object obj) {
        return super.delete(obj)
    }

    /**
     * 取前n条记录
     */
    public Pager limit(int n) {
        return createPager(1, n)
    }

    public List<Record> query(String tableNameOrSql) {
        if (tableNameOrSql.toLowerCase().contains("select")) {
            return super.query("(" + tableNameOrSql + ") autoTable", null, null)
        } else {
            return super.query(tableNameOrSql, null, null)
        }
    }

    public List<Record> query(String tableNameOrSql, Condition condition, Pager pager) {
        if (tableNameOrSql.toLowerCase().contains("select")) {
            return super.query("(" + tableNameOrSql + ") autoTable", condition, pager)
        } else {
            return super.query(tableNameOrSql, condition, pager)
        }
    }

    public <T> List<T> query(Class<T> classOfT, Condition cnd) {
        return query(classOfT, cnd, null)
    }

    public int count(String tableNameOrSql) {
        return count(tableNameOrSql, null)
    }

    public int count(String tableNameOrSql, Condition condition) {
        if (tableNameOrSql.toLowerCase().contains("select")) {
            return super.count("(" + tableNameOrSql + ") autoTable", condition)
        } else {
            return super.count(tableNameOrSql, condition)
        }
    }

    @Override
    public int update(Object obj) {
        initUpdateTime(obj)
        return super.update(obj)
    }

    @Override
    public int update(Object obj, String regex) {
        initUpdateTime(obj)
        return super.update(obj, regex)
    }

    @Override
    public int updateIgnoreNull(Object obj) {
        initUpdateTime(obj)
        return super.updateIgnoreNull(obj)
    }

    @Override
    public <T> T updateWith(T obj, String regex) {
        initUpdateTime(obj)
        return super.updateWith(obj, regex)
    }

    private void initUpdateTime(Object obj) {
        if (obj instanceof CommonEntity) {
            CommonEntity entity = (CommonEntity) obj
            entity.setUpdateTime(new Date())
        }
    }

    /**
     * 自动扫描指定包路径，并创建实体表
     */
    public void buildTable(boolean dropIfExists, String... packages) {
        for (String it : packages) {
            for (Class<?> clazz : Scans.me().scanPackage(it)) {
                if (clazz.getAnnotation(Table.class) != null) {
                    create(clazz, dropIfExists)
                }
            }
        }
    }
}