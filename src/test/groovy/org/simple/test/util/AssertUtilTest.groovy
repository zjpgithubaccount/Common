package org.simple.test.util

import org.junit.Test
import org.simple.base.util.AssertUtil

/**
 * @author zhangjp
 */
class AssertUtilTest {
    @Test
    void testError() {
        AssertUtil.error(new Exception("error1"))
        AssertUtil.error("error2", Exception)
    }

    @Test
    void testNotNull() {
        AssertUtil.notNull(null, new Exception("null"))
        AssertUtil.notNull("1", new Exception("1 is null"))
    }

    @Test
    void testAssertTrue() {
        AssertUtil.assertTrue(true, new Exception("should be true"))
        AssertUtil.assertTrue(false, new Exception("should be true"))
    }

    @Test
    void testAssertNotEmpty() {
        AssertUtil.notEmpty(["1", "2"], new Exception("should not be empty"))
        AssertUtil.notEmpty([], new Exception("[]"))
        AssertUtil.notEmpty(null, new Exception("null"))
    }
}
