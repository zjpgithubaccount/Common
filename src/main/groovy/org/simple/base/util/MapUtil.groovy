package org.simple.base.util
/**
 * @author zhangjp
 */
class MapUtil {
    /**
     * 判断所有值是否都为空
     */
    static boolean isEmptyValue(Map self) {
        if (!self) {
            return false
        }

        for (value in self.values()) {
            if (StringUtil.isNotBlank(value)) {
                return false
            }
        }

        return true
    }

    /**
     * 将对象转成Map
     * @return
     */
    static Map<String, Object> toMap(Object o) {
        return o.properties.findAll {
            if (it.value == null) {
                return false
            }

            if (it.key == 'class' ||
                    it.key.toString().startsWith('__')) {
                return false
            }

            return true
        }
    }

    /**
     * 将Map转成对象
     * @param map
     * @param classz
     * @return
     */
    static <T> T toObject(Map<String, Object> map, Class<T> classz) {
        T instance = classz.newInstance()
        //TODO: 测试不通过
//        map.each { key, value ->
//            instance.properties.each {
//                if(it.key == key) {
//                    instance."${key.toString()}" = map.get(key)
//                }
//            }
//        }

        return instance
    }
}
