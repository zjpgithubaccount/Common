package org.simple.test.exception

import org.junit.Test
import org.simple.base.exception.ErrorCode

/**
 * @author zhangjp
 */
class ErrorCodeTest {
    @Test
    void testErrorCode() {
        TestErrorCode.ERROR1.error()
        TestErrorCode.ERROR1.assertTrue(true)
        TestErrorCode.ERROR2.assertFalse(false)
        TestErrorCode.ERROR1.error("this is Error1")
    }
}

enum TestErrorCode implements ErrorCode {
    ERROR1("00001", "ERROR1"),
    ERROR2("00002", "ERROR2")

    TestErrorCode(String code, String message) {
        this.code = code
        this.message = message
    }
}