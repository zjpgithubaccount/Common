package org.simple.test.util

import org.junit.Test
import org.simple.base.util.DateUtil

/**
 * @author zhangjp
 */
class DateUtilTest {
    @Test
    void testFormatDate() {
        println DateUtil.formatDate(new Date())
        println DateUtil.formatDate(new Date(), "yyyy-MM-dd")
        println DateUtil.formatDate(new Date(), null)
    }

    @Test
    void parseDate() {
        println DateUtil.parseDate("2015-04-15", "yyyy-MM-dd").getDateTimeString()
        println DateUtil.parseDate("2015-04-15 12:33:02", "yyyy-MM-dd HH:mm:ss").getDateTimeString()
    }
}
