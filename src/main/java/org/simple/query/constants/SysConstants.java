package org.simple.query.constants;

public class SysConstants {
    /**
     * 是的数字值
     */
    public static final Integer YES = 1;

    /**
     * 否的数字值
     */
    public static final Integer NO = 0;

    /**
     * 规则：大于
     */
    public static final String RULE_BIG = ">";

    /**
     * 规则：大于等于
     */
    public static final String RULE_BIG_EQUAL = ">=";

    /**
     * 规则：等于
     */
    public static final String RULE_EQUAL = "=";

    /**
     * 默认动态语句规则
     */
    public static String DEFAULT_DYNAMIC_RULE = "\\$\\[(.+?)\\]";

    /**
     * 默认占位符前缀规则
     */
    public static String DEFAULT_PLACEHOLDER_PREFIX = "\\$";

    /**
     * 默认参数前缀规则
     */
    public static String DEFAULT_PARAM_PREFIX = "@";

    /**
     * 默认参数规则
     */
    public static final String DEFAULT_PARAM_RULE = "(\\w+)\\b";

    /**
     * 清除Where后的关键字
     */
    public static final String CLEAR_AFTER_WHERE = "(where\\s+?)(\\band\\b|\\bor\\b)";

    /**
     * 清除Where关键字
     */
    public static final String CLEAR_WHERE = "(where)\\s+?($|order\\s+?by\\b|group\\s+?by\\b|\\bunique\\b)";

    /**
     * 清除换行
     */
    public static final String CLEAR_NEWLINE = "\n|\r";

    /**
     * 条件：and
     */
    public static final String OPERATON_AND = "and";

    /**
     * 条件：is
     */
    public static final String OPERATON_IS = "is";

    /**
     * 条件：null
     */
    public static final String OPERATON_NULL = "null";

    /**
     * 条件：between
     */
    public static final String OPERATON_BETWEEN = "between";

    /**
     * 默认追加规则
     */
    public static final String DEFAULT_APPEND_RULE = "append_cnd\\b";
}

