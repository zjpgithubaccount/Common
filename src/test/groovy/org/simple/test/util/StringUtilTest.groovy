package org.simple.test.util

import org.junit.Assert
import org.junit.Test
import org.simple.base.util.StringUtil

/**
 * @author zhangjp
 */
class StringUtilTest {
    @Test
    void testIsNullOrEmpty() {
        String str1 = ""
        String str2 = null
        String str3 = "zhangjp"

        Assert.assertTrue(StringUtil.isNullOrEmpty(str1) && StringUtil.isNullOrEmpty(str2))
        Assert.assertFalse(StringUtil.isNullOrEmpty(str3))
    }
}
