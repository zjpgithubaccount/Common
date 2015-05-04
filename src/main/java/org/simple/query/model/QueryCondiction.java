package org.simple.query.model;

import org.simple.query.constants.SysConstants;

import java.io.Serializable;

/**
 * 条件bean
 *
 * @author Jay.Wu
 */
public class QueryCondiction implements Serializable {
    /**
     * @return 属性名称
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property 属性名称
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return 属性值
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value 属性值
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the anotherValue
     */
    public Object getAnotherValue() {
        return anotherValue;
    }

    /**
     * @param anotherValue the anotherValue to set
     */
    public void setAnotherValue(Object anotherValue) {
        this.anotherValue = anotherValue;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 属性名称
     */
    private String property;
    /**
     * 属性值
     */
    private Object value;
    /**
     * 另一个属性名称
     */
    private Object anotherValue;
    /**
     * 条件符号
     */
    private String operation = SysConstants.RULE_EQUAL;
}
