package org.simple.base.nutz.dao

import org.nutz.dao.Cnd
import org.nutz.dao.util.cri.SqlExpression
import org.simple.base.serializable.SerializableHelper
import org.simple.base.util.ListUtil
import org.simple.base.util.OrderCnd
import org.simple.base.util.PropertyCnd
import org.simple.base.util.StringUtil

/**
 * Cnd增强类
 * <ul>
 * <li>可以自动过滤空数据
 * <li>可以快速创建自定义sql
 * </ul>
 *
 * @author Jay.Wu
 */
public class CndPlus extends Cnd {

    protected CndPlus(SqlExpression e) {
        super(e)
    }

    public CndPlus ands(List<SqlExpression> exps) {
        for (SqlExpression SqlExpression : exps) {
            and(SqlExpression)
        }

        return this
    }

    public CndPlus ors(List<SqlExpression> exps) {
        for (SqlExpression SqlExpression : exps) {
            or(SqlExpression)
        }

        return this
    }

    private static CndPlus Cnd2CndPlus(String name, String op, Object value, boolean ignoreNull) {
        return new CndPlus(exp(name, op, value, ignoreNull))
    }

    public static SqlExpression exp(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return exp("1", "=", 1)
        }

        return exp(name.trim(), op, value)
    }

    /**
     * 判断表达式是否空
     */
    private static boolean isEmpty(String op, Object value) {
        // IS (NOT) NULL 为特殊情况
        if (op.toUpperCase().trim().startsWith("IS") && value == null) {
            return false
        } else {
            // 判断字符串是否为空
            if (value instanceof String) {
                if (StringUtil.isBlank(value)) {
                    return true
                }
            } else if (value == null) {
                return true
            } else if (value instanceof Collection) {
                return ListUtil.isEmpty((Collection<?>) value)
            } else if (value instanceof Object[]) {
                return ListUtil.isEmpty((Object[]) value)
            }
        }

        return false
    }

    /**
     * “等于”快速操作，忽略空值
     */
    public static CndPlus where(String name, Object value) {
        return where(name, "=", value)
    }

    /**
     * “等于”快速操作，不忽略空值
     */
    public static CndPlus whereX(String name, Object value) {
        return where(name, "=", value, false)
    }

    public static CndPlus where(String name, String op, Object value) {
        return where(name, op, value, true)
    }

    public static CndPlus whereX(String name, String op, Object value) {
        return where(name, op, value, false)
    }

    public static CndPlus where(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return Cnd2CndPlus("1", "=", 1, ignoreNull)
        }

        return Cnd2CndPlus(name, op, value, ignoreNull)
    }

    public static CndPlus whereX(SqlExpression expression) {
        return (CndPlus) where(expression)
    }

    public CndPlus andX(SqlExpression expression) {
        return (CndPlus) and(expression)
    }

    public CndPlus orX(SqlExpression expression) {
        return (CndPlus) or(expression)
    }

    public CndPlus and(String name, Object value) {
        return and(name, "=", value)
    }

    public CndPlus and(String name, String op, Object value) {
        return and(name, op, value, true)
    }

    public CndPlus andX(String name, Object value) {
        return andX(name, "=", value)
    }

    public CndPlus andX(String name, String op, Object value) {
        return and(name, op, value, false)
    }

    public CndPlus and(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return this
        }

        super.and(exp(name, op, value, ignoreNull))
        return this
    }

    public CndPlus or(String name, Object value) {
        return or(name, "=", value)
    }

    public CndPlus or(String name, String op, Object value) {
        return or(name, op, value, true)
    }

    public CndPlus orX(String name, Object value) {
        return orX(name, "=", value)
    }

    public CndPlus orX(String name, String op, Object value) {
        return or(name, op, value, false)
    }

    public CndPlus or(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return this
        }

        super.or(exp(name, op, value))
        return this
    }

    public CndPlus andNot(String name, Object value) {
        return andNot(name, "=", value)
    }

    public CndPlus andNot(String name, String op, Object value) {
        return andNot(name, op, value, true)
    }

    public CndPlus andNotX(String name, Object value) {
        return andNotX(name, "=", value)
    }

    public CndPlus andNotX(String name, String op, Object value) {
        return andNot(name, op, value, false)
    }

    public CndPlus andNot(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return this
        }

        super.andNot(exp(name, op, value, ignoreNull))
        return this
    }

    public CndPlus orNot(String name, Object value) {
        return orNot(name, "=", value)
    }

    public CndPlus orNot(String name, String op, Object value) {
        return orNot(name, op, value, true)
    }

    public CndPlus orNotX(String name, Object value) {
        return orNotX(name, "=", value)
    }

    public CndPlus orNotX(String name, String op, Object value) {
        return orNot(name, op, value, false)
    }

    public CndPlus orNot(String name, String op, Object value, boolean ignoreNull) {
        if (ignoreNull && isEmpty(op, value)) {
            return this
        }

        super.orNot(exp(name, op, value, ignoreNull))
        return this
    }

    public static CndPlus putAll(Map<String, ?> filter) {
        return putAllCnd(PropertyCnd.fromMap(filter)).orderBy(filter)
    }

    public static CndPlus putAllCnd(List<PropertyCnd> cnds) {
        if (ListUtil.isEmpty(cnds)) {
            return where("", null)
        }

        CndPlus cndPlus = null

        for (PropertyCnd propertyCnd : cnds) {
            if (cndPlus == null) {
                cndPlus = where(propertyCnd.getName(), propertyCnd.getOp(), propertyCnd.getValue())
            } else {
                cndPlus.and(propertyCnd.getName(), propertyCnd.getOp(), propertyCnd.getValue())
            }
        }

        return cndPlus
    }

    public CndPlus orderBy(Map<String, ?> filter) {
        return orderByCnd(OrderCnd.fromMap(filter), null)
    }

    public CndPlus orderBy(Map<String, ?> filter, OrderCnd defaultOrderCnd) {
        return orderByCnd(OrderCnd.fromMap(filter), defaultOrderCnd)
    }

    public CndPlus orderByCnd(List<OrderCnd> orders, OrderCnd defaultOrderCnd) {
        if (ListUtil.isEmpty(orders)) {
            if (defaultOrderCnd == null) {
                return this
            } else {
                orders = new ArrayList<OrderCnd>()
                orders.add(defaultOrderCnd)
            }
        }

        for (OrderCnd order : orders) {
            switch (order.getBy()) {
                case ASC:
                    this.asc(order.getName())
                    break

                case DESC:
                    this.desc(order.getName())
                    break
            }
        }

        return this
    }

    /**
     * 根据给定时间，以及开始时间和结束时间字段名称，生成查出有效记录的条件
     *
     * @param fromDateName 开始时间字段名称
     * @param thruDateName 结束时间字段名称
     * @param fromDate 开始时间
     * @param thruDate 截止时间
     * @return SqlExpression
     */
    public static SqlExpression effectiveCnd(String fromDateName, String thruDateName, Date fromDate, Date thruDate) {
        return exps(exps(fromDateName, "<=", fromDate).or(fromDateName, "is", null))
                .and(exps(thruDateName, ">", thruDate).or(thruDateName, "is", null))
    }

    /**
     * 根据给定时间，生成查出有效记录的条件
     *
     * @param now 查询时间
     * @return SqlExpression
     */
    public static SqlExpression effectiveCnd(Date now) {
        return effectiveCnd("fromDate", "thruDate", now, now)
    }


    /**
     * 根据给定时间，生成查出有效记录的条件
     *
     * @param fromDate 开始时间
     * @param thruDate 截止时间
     * @return SqlExpression
     */
    public static SqlExpression effectiveCnd(Date fromDate, Date thruDate) {
        return effectiveCnd("fromDate", "thruDate", fromDate, thruDate)
    }

    /**
     * 生成当前时间所有有效记录的条件
     *
     * @return SqlExpression
     */
    public static SqlExpression effectiveCnd() {
        return effectiveCnd(new Date())
    }

    /**
     * 生成当前日期所有有效记录的条件
     *
     * @return SqlExpression
     */
    public static SqlExpression effectiveDayCnd() {
        Date now = new Date()
        return effectiveCnd(now, now.toDate("yyyy-MM-dd"))
    }

    public CndPlus copy() {
        return SerializableHelper.clone(this)
    }
}
