package org.simple.base.util

import org.simple.base.model.Function

class PropertyQueryUtil {

    static String CND_REG = /\((.+)\)(.+)/

    static <T> List<T> findAll(T[] self, PropertyCnd modelQueryCnd) {
        List<T> result = []

        for (T obj in self) {
            if (match(obj, modelQueryCnd)) {
                result << obj
            }
        }

        return result
    }

    static <T> List<T> findAll(Collection<T> self, PropertyCnd modelQueryCnd) {
        List<T> result = []

        for (T obj in self) {
            if (match(obj, modelQueryCnd)) {
                result << obj
            }
        }

        return result
    }

    static <T> T find(T[] self, PropertyCnd modelQueryCnd) {
        for (T obj in self) {
            if (match(obj, modelQueryCnd)) {
                return obj
            }
        }

        return null
    }

    static <T> T find(Collection<T> self, PropertyCnd modelQueryCnd) {
        for (T obj in self) {
            if (match(obj, modelQueryCnd)) {
                return obj
            }
        }

        return null
    }

    static <T> boolean match(T self, PropertyCnd modelQueryCnd) {
        if (!modelQueryCnd) {
            return true
        }

        if (modelQueryCnd.function) {
            if (!modelQueryCnd.function.run(self))
                return false
        }

        if (modelQueryCnd.closure) {
            if (!modelQueryCnd.closure.call(self))
                return false
        }

        for (PropertyCnd cnd in modelQueryCnd.and) {
            if (!match(self, cnd.name, cnd.op, cnd.value)) {
                return false
            }
        }

        for (PropertyCnd cnd in modelQueryCnd.or) {
            return true
        }

        return true
    }

    static <E> boolean match(E[] self, String name, String op, Object value) {
        for (E e in self) {
            if (!match(e, name, op, value)) {
                return false
            }
        }

        return true
    }

    static <E> boolean match(Collection<E> self, String name, String op, Object value) {
        for (E e in self) {
            if (!match(e, name, op, value)) {
                return false
            }
        }

        return true
    }

    static boolean match(Object self, String name, String op, Object value) {
        // 无属性名称，忽略
        if (!name) {
            return true
        }

        Object objValue = self.getAt(name)
        op = op.toLowerCase()

        switch (op) {
            case '=':
                return objValue == value

            case 'is':
                return objValue == value

            case '!=':
                return objValue != value

            case '<>':
                return objValue != value

            case 'not':
                return objValue != value

            case 'is not':
                return objValue != value

            case 'not is':
                return objValue != value

            case '>':
                return objValue > value

            case '<':
                return objValue < value

            case '>=':
                return objValue >= value

            case '<=':
                return objValue <= value

            case 'like':
                return LIKE(objValue.toString(), value?.toString())

            case 'not like':
                return !LIKE(objValue.toString(), value?.toString())

            case 'in':
                return IN(objValue, (List) value)

            case 'not in':
                return !IN(objValue, (List) value)

            case 'contains':
                return CONTAINS((List) objValue, (List) value)

            case 'not contains':
                return !CONTAINS((List) objValue, (List) value)

            default:
                throw new RuntimeException('不支持此操作符号: ' + op)
                break
        }

        return false
    }

    private static boolean CONTAINS(List objValue, List value) {
        if (objValue == null && value == null) {
            return true
        } else if (objValue == null || value == null) {
            return false
        }

        return objValue.containsAll(value)
    }

    private static boolean LIKE(String objValue, String value) {
        if (objValue == null && value == null) {
            return true
        } else if (objValue == null || value == null) {
            return false
        }

        if (value.startsWith('%') && value.endsWith('%')) {
            return objValue.contains(value - '%' - '%')
        } else if (value.startsWith('%')) {
            return objValue.endsWith(value - '%')
        } else if (value.endsWith('%')) {
            return objValue.startsWith(value - '%')
        } else {
            return objValue == value
        }
    }

    private static boolean IN(Object objValue, List value) {
        if (objValue == null || !value) {
            return false
        }

        if (objValue instanceof Collection) {
            for (Object o : objValue) {
                if (value.contains(o)) {
                    return true
                }
            }
        }

        return value.contains(objValue)
    }
}

class OrderCnd {
    enum By {
        ASC,
        DESC

        static By from(String name) {
            if (name) {
                return values().find { By t ->
                    t.name() == name.toUpperCase()
                }
            } else {
                return ASC
            }
        }
    }

    String name

    By by = By.ASC

    public static List<OrderCnd> fromMap(Map<String, ?> filters) {
        List<OrderCnd> result = []

        for (entry in filters) {
            def matcher = entry.key =~ /\((.+)\)(.+)/

            if (matcher.find()) {
                String op = matcher[0][1]
                String name = matcher[0][2]

                if (op.trim().toLowerCase() == 'OrderBy'.toLowerCase()) {

                    result.add(new OrderCnd(
                            name: name,
                            by: OrderCnd.By.from((String) entry.value)
                    ))
                }
            }
        }

        return result
    }

    String toString() {
        JsonUtil.toJson(this)
    }
}

class PropertyCnd {

    List<PropertyCnd> and = []

    List<PropertyCnd> or = []

    String name

    String op

    Object value

    transient Function function

    transient Closure<Boolean> closure

    /**
     * 自定义条件，可供Java调用
     */
    static <K> PropertyCnd where(Function<K, Boolean> c) {
        return new PropertyCnd(function: c)
    }

    /**
     * 自定义条件，Groovy专用
     */
    static PropertyCnd where(Closure<Boolean> c) {
        return new PropertyCnd(closure: c)
    }

    static PropertyCnd whereX(String name, String op = '=', Object value) {
        PropertyCnd cnd = new PropertyCnd(
                name: name,
                op: op,
                value: value
        )

        cnd.and << cnd
        return cnd
    }

    static PropertyCnd where(String name, String op = '=', Object value) {
        if (!op.toLowerCase().contains('is') && StringUtil.isBlank(value)) {
            return new PropertyCnd()
        }

        return whereX(name, op, value)
    }

    PropertyCnd andX(String name, String op = '=', Object value) {
        PropertyCnd cnd = new PropertyCnd(
                name: name,
                op: op,
                value: value)

        and << cnd
        return this
    }

    PropertyCnd and(String name, String op = '=', Object value) {
        if (!op.toLowerCase().contains('is') && StringUtil.isBlank(value)) {
            return this
        }

        return andX(name, op, value)
    }

    PropertyCnd orX(String name, String op = '=', Object value) {
        PropertyCnd cnd = new PropertyCnd(
                name: name,
                op: op,
                value: value
        )

        or << cnd
        return this
    }

    PropertyCnd or(String name, String op = '=', Object value) {
        if (!op.toLowerCase().contains('is') && StringUtil.isBlank(value)) {
            return this
        }

        return orX(name, op, value)
    }

    public static PropertyCnd from(String name, Object value) {
        String op = '='

        def matcher = name =~ PropertyQueryUtil.CND_REG

        matcher.each { String all, String a, String b ->
            op = a.replace('_', ' ')
            name = b
        }

        if (op.trim().toLowerCase() == 'OrderBy'.toLowerCase()) {
            return null
        }

        return where(name, op, value)
    }

    /**
     * 快速将所有条件变为 and 条件
     */
    public static PropertyCnd putAll(Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) {
            return where('', null)
        }

        PropertyCnd cnd = null

        for (entry in filter) {
            def temp = from(entry.key, entry.value)

            if (temp?.op?.toLowerCase()?.contains('is') || StringUtil.isNotBlank(temp?.value)) {
                if (cnd == null) {
                    cnd = temp
                } else {
                    cnd.and << temp
                }
            }
        }

        return cnd
    }

    public static List<PropertyCnd> fromMap(Map<String, ?> filters) {
        List<PropertyCnd> result = []

        for (entry in filters) {
            if (entry.key.toLowerCase().contains('(is') || StringUtil.isNotBlank(entry.value)) {
                def cnd = from(entry.key, entry.value)

                if (cnd) {
                    result.add(cnd)
                }
            }
        }

        return result
    }

    String toString() {
        return String.format('%s %s %s',
                name, op, value
        )
    }
}