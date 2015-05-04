package org.simple.base.nutz.constants

import org.simple.base.constants.BaseConstants

/**
 * @author Jay.Wu
 */
public class NutzConstants extends BaseConstants{

    public static final String COMM_VERSION_INCOMPATIBLE_CODE = "COMM_VERSION_INCOMPATIBLE"
    public static final String COMM_VERSION_INCOMPATIBLE_MESSAGE = "该客户端已过时，请升级到 %s 或更高版本"

    public static final String COMM_1001_CODE = "COMM_1001"
    public static final String COMM_1001_MESSAGE = "Api参数缺少泛型声明"

    public static final String COMM_1002_CODE = "COMM_1002"
    public static final String COMM_1002_MESSAGE = "请求消息体Json格式不符合接口要求"
}
