package org.simple.test.util

import groovy.transform.CompileStatic
import org.junit.Test
import org.simple.base.util.AssertUtil
import org.simple.base.util.ListUtil

/**
 * @author zhangjp
 */
@CompileStatic
class ListUtilTest {
    @Test
    void testNotEmpty() {
        AssertUtil.assertTrue(ListUtil.isEmpty([]), new Exception("is not empty"))
        AssertUtil.assertTrue(ListUtil.isNotEmpty(["1"]), new Exception("is empty"))
        AssertUtil.assertTrue(ListUtil.first(["1", "2"]) == "1", new Exception("frist error"))
    }
}
