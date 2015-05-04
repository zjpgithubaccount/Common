package org.simple.query.util;

import org.simple.query.constants.SysConstants;
import org.simple.query.core.QueryAppend;
import org.simple.query.core.QueryFilter;
import org.simple.query.model.QueryCondiction;
import org.simple.base.serializable.SerializableHelper;
import org.simple.base.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtil implements Cloneable {

    private QueryAppend queryAppend = new QueryAppend();

    private QueryFilter queryFilter = new QueryFilter();

    private String queryKey;

    private String queryString;

    private Object pager;

    private boolean convert = true;

    private List<Object> paramValues = new ArrayList<Object>();

    public QueryUtil() {
    }

    public QueryUtil(String key) {
        this.queryKey = key;
    }

    public static QueryUtil createByKey(String key) {
        return new QueryUtil(key);
    }

    public static QueryUtil createByKey(String key, Object pager) {
        return createByKey(key).setPager(pager);
    }

    public static QueryUtil createBySql(String sql) {
        QueryUtil q = new QueryUtil();
        q.setQueryString(sql);
        return q;
    }

    public static QueryUtil createBySql(String sql, Object pager) {
        return createBySql(sql).setPager(pager);
    }

    public void process() {
        queryString = queryString.replaceAll(SysConstants.CLEAR_NEWLINE, " ");
        queryFilter.setQueryString(queryString);
        queryFilter.process();
        queryString = queryFilter.getQueryString();

        boolean hasAppend = false;
        Pattern p = Pattern.compile(queryFilter.getParamRule(), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(queryString);

        while (m.find()) {
            String paramGroup = m.group(0).trim();
            String param = m.group(1).trim();
            Object value = queryFilter.get(param);

            if (StringUtil.find(queryAppend.getAppendRule(), paramGroup, 0) != null) {
                queryAppend.setQueryString(queryString);
                queryString = queryAppend.process();
                paramValues.addAll(queryAppend.getParamValues());
                hasAppend = true;
            } else {
                if (StringUtil.isBlank(value)) {
                    throw new RuntimeException("Missing param: " + paramGroup + ", using key: " + queryKey);
                }

                // queryString = queryString.replaceAll(paramGroup + "\\b",
                // "? ");
                paramValues.add(value);
            }
        }

        if (!hasAppend) {
            queryAppend.setQueryString(queryString);
            queryString = queryAppend.process();
            paramValues.addAll(queryAppend.getParamValues());
        }

        m = p.matcher(queryString);

        if (convert) {
            while (m.find()) {
                String paramGroup = m.group(0).trim();
                queryString = queryString.replaceAll(paramGroup + "\\b", "? ");
            }
        }

        queryClear();
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    private void queryClear() {
        queryString = StringUtil.replace(SysConstants.CLEAR_AFTER_WHERE, queryString, 1);
        queryString = StringUtil.replace(SysConstants.CLEAR_WHERE, queryString, 2);
    }

    /**
     * 自动封装request中以q_开头的查询条件
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public QueryUtil initRequest(HttpServletRequest request) {
        Map<String, ?> map = request.getParameterMap();
        String q = "q_";
        // like 开头需要增加%
        String b = "b_";
        // like 结尾需要增加%
        String e = "e_";

        for (String key : map.keySet()) {
            int p = key.indexOf(q);

            if (p == 0) {
                Object valueObj = request.getParameter(key);

                if (valueObj instanceof String) {
                    String value = (String) valueObj;

                    if (StringUtil.isBlank(value)) {
                        continue;
                    }

                    request.setAttribute(key, value);

                    // 空格使用@代替
                    value = StringUtil.join(value.split("@"), " ");

                    if (key.contains(q + b + e)) {
                        key = key.substring(6);

                        // 当从网页传入的参数和代码中手工put的key相同时，优先取代码中的key
                        if (getParam(key) == null) {
                            put(key, value, "%", "%");
                        }
                    } else if (key.contains(q + b)) {
                        key = key.substring(4);

                        if (getParam(key) == null) {
                            put(key, value, "%", null);
                        }
                    } else if (key.contains(q + e)) {
                        key = key.substring(4);

                        if (getParam(key) == null) {
                            put(key, value, null, "%");
                        }
                    } else {
                        key = key.substring(2);

                        if (getParam(key) == null) {
                            put(key, value);
                        }
                    }
                }
            }
        }

        return this;
    }

    /**
     * 给sql中的动态参数赋值处理，like专用
     *
     * @param key
     * @param value
     * @param b     前置%
     * @param e     后置%
     */
    public void put(String key, String value, String b, String e) {
        if (StringUtil.isNotBlank(value)) {
            if (b == null) {
                b = "";
            }

            if (e == null) {
                e = "";
            }

            put(key, b + value + e);
        }
    }

    public QueryUtil append(String key, Object value) {
        queryAppend.put(key, SysConstants.RULE_EQUAL, value);
        return this;
    }

    public QueryUtil append(String key, String operation, Object value) {
        queryAppend.put(key, operation, value);
        return this;
    }

    public Map<String, QueryCondiction> condictions() {
        return queryAppend.params();
    }

    public QueryCondiction getQueryCondiction(String key) {
        return queryAppend.get(key);
    }

    public Map<String, Object> params() {
        return queryFilter.params();
    }

    public QueryUtil put(String key, Object value) {
        queryFilter.put(key, value);
        return this;
    }

    public QueryUtil putAll(Map<String, Object> m) {
        if (m != null) {
            queryFilter.putAll(m);
        }

        return this;
    }

    public Object getParam(String key) {
        return queryFilter.get(key);
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public List<Object> getParamValues() {
        return paramValues;
    }

    public QueryFilter getQueryFilter() {
        return queryFilter;
    }

    public void setQueryFilter(QueryFilter queryFilter) {
        this.queryFilter = queryFilter;
    }

    public QueryAppend getQueryAppend() {
        return queryAppend;
    }

    public void setQueryAppend(QueryAppend queryAppend) {
        this.queryAppend = queryAppend;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPager() {
        return (T) pager;

    }

    public QueryUtil setPager(Object pager) {
        this.pager = pager;
        return this;
    }

    public boolean isConvert() {
        return convert;
    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    public QueryUtil clone() {
        return (QueryUtil) SerializableHelper.clone(this);
    }
}
