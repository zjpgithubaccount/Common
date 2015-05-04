package org.simple.query.core;

import org.simple.query.constants.SysConstants;
import org.simple.query.model.QueryCondiction;
import org.simple.base.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryAppend {
    public Map<String, QueryCondiction> params() {
        return paramMap;
    }

    public void put(String key, QueryCondiction value) {
        paramMap.put(key, value);
    }

    public QueryCondiction get(String key) {
        return paramMap.get(key);
    }

    public QueryCondiction put(String key, String operation, Object value) {
        if (StringUtil.isBlank(value)) {
            paramMap.remove(key);
            return null;
        }


        QueryCondiction condiction = new QueryCondiction();
        condiction.setProperty(key);
        condiction.setOperation(operation);
        condiction.setValue(value);
        put(key, condiction);
        return condiction;
    }

    public QueryCondiction append(String key, String operation, String value, String anotherValue) {
        QueryCondiction condiction = put(key, operation, value);
        if (condiction != null) {
            condiction.setAnotherValue(anotherValue);
        }


        return condiction;
    }

    public String process() {
        String appendcString = appendCondictions();

        if (StringUtil.find(appendRule, queryString, 0) != null) {
            queryString = queryString.replaceAll(appendRule, appendcString);
        } else if (!appendcString.equals("")) {
            queryString = queryString + " " + SysConstants.OPERATON_AND + " " + appendcString;
        }


        return queryString;
    }

    private String appendCondictions() {
        if (paramMap.isEmpty()) {
            return "";
        }


        StringBuffer whereBuilder = new StringBuffer("");

        for (QueryCondiction condictionsBean : paramMap.values()) {
            if (whereBuilder.length() == 0) {
                whereBuilder.append(" ");
            } else {
                whereBuilder.append(SysConstants.OPERATON_AND).append(" ");
            }


            // 处理 "is (not) null" 条件
            if (StringUtil.containsIgnoreCase(condictionsBean.getOperation(), SysConstants.OPERATON_IS) && (condictionsBean.getValue() == null || StringUtil.equalsIgnoreCase(condictionsBean.getOperation(), SysConstants.OPERATON_NULL))) {
                wrap(whereBuilder, condictionsBean, null);
                continue;
            }


            wrap(whereBuilder, condictionsBean, SysConstants.DEFAULT_PARAM_PREFIX + condictionsBean.getProperty());
            paramValues.add(condictionsBean.getValue());

            // 处理between条件
            if (StringUtil.containsIgnoreCase(condictionsBean.getOperation(), SysConstants.OPERATON_BETWEEN)) {
                whereBuilder.append(SysConstants.OPERATON_AND).append(" " + SysConstants.DEFAULT_PARAM_PREFIX + condictionsBean.getProperty() + "_ ");
                paramValues.add(condictionsBean.getAnotherValue());
            }

        }


        return whereBuilder.toString();
    }

    private void wrap(StringBuffer whereBuilder, QueryCondiction condiction, Object value) {
        whereBuilder.append(condiction.getProperty()).append(" ").append(condiction.getOperation()).append(" ").append(value == null ? condiction.getValue() : value).append(" ");
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

    public String getAppendRule() {
        return appendRule;
    }

    public void setAppendRule(String appendRule) {
        this.appendRule = appendRule;
    }

    private String queryString;
    private Map<String, QueryCondiction> paramMap = new HashMap<String, QueryCondiction>();
    private List<Object> paramValues = new ArrayList<Object>();
    private String appendRule = SysConstants.DEFAULT_PARAM_PREFIX + SysConstants.DEFAULT_APPEND_RULE;
}
