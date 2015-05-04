package org.simple.test.util

import org.junit.Test
import org.simple.base.util.MapUtil

/**
 * @author zhangjp
 */
class MapUtilTest {
    @Test
    void testToMap() {
        Map<String, Object> result = MapUtil.toMap(new Person())
        result.each {
            println(it)
        }
    }

    @Test
    void testToObject() {
        Map<String, Object> map = [ name: "zjp", age: "27"]
        Person person = MapUtil.toObject(map, Person)
    }

    class Person {
        String name;
        int age;
    }
}
