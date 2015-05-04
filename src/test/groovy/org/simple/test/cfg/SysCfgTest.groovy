package org.simple.test.cfg

import org.junit.Test
import org.simple.cfg.api.SysCfg

/**
 * @author zhangjp
 */
class SysCfgTest {
    @Test
    void testGetProperties() {
        SysCfg.load("prop")
        println SysCfg.get().getString("name")
        println SysCfg.get().getInt("age")

        println SysCfg.get().getBoolean("uas.self")
        println SysCfg.get().getString("uas.applicationId")
    }
}
