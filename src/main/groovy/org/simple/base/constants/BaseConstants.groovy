package org.simple.base.constants

/**
 * @author Jay.Wu
 */
public class BaseConstants {
    public static final Integer YES = 1

    public static final Integer NO = 0

    public static final int SUCCESSFUL = 1

    public static final int FAILURE = 0

    /**
     * 男性
     */
    public static final int SEX_MALE = 1

    /**
     * 女性
     */
    public static final int SEX_FEMALE = 0

    /**
     * 其他
     */
    public static final int SEX_OTHER = -1

    /**
     * 规则：大于
     */
    public static final String RULE_BIG = ">"

    /**
     * 规则：大于等于
     */
    public static final String RULE_BIG_EQUAL = ">="

    /**
     * 规则：等于
     */
    public static final String RULE_EQUAL = "="

    /**
     * 默认每页显示记录数目
     */
    public static final Integer PAGE_SIZE = 15

    /**
     * 系统帐号
     */
    public static final String SYSTEM = "0"

    /**
     * 网页中传递的参数名
     */
    public static final String PAGE_NAME = "page"

    /**
     * 新增记录状态位
     */
    public static final int INSERTED = 100

    /**
     * 删除记录状态位
     */
    public static final int DELETED = 102

    /**
     * 更新记录状态位
     */
    public static final int UPDATED = 102

    /**
     * MQ运行模式：内存模式
     */
    public static final int MQ_MODE_MEMORY = 0

    /**
     * MQ运行模式：存储模式
     */
    public static final int MQ_MODE_PERSISTENT = 1

    /**
     * 默认重建页面时间：60s
     */
    public static final int PAGE_REBUILD_TIME = 60

    /**
     * 优先级：高
     */
    public static final int PRIORITY_RELEVANCE = 301

    /**
     * 优先级：高
     */
    public static final int PRIORITY_HIGH = 300

    /**
     * 优先级：中
     */
    public static final int PRIORITY_MIDDLE = 200

    /**
     * 优先级：低
     */
    public static final int PRIORITY_LOW = 100

    /**
     * 优先级：忽略
     */
    public static final int PRIORITY_IGNORE = 0

    /**
     * 任务Kind
     */
    public static final String KIND_JOB = "JOB"

    /**
     * 导入链接Kind
     */
    public static final long KIND_IMPORT_LINK = 7L

    /**
     * 扫描导入最大页码Code
     */
    public static final long KIND_SCAN = 10l

    /**
     * 扫描导入最大页码Code
     */
    public static final String KIND_SCAN_MAX_PAGE = "maxPageNum"

    /**
     * 字符编码UTF-8
     */
    public static final String ENC_UTF8 = "UTF-8"

    /**
     * 用户session中的Key
     */
    public static final String USER_KEY = "auth"

    /**
     * 登陆验证方法
     */
    public static final String LOGIN_ACTION = "login"

    /**
     * 退出方法
     */
    public static final String LOGOUT_ACTION = "logout"

    /**
     * 默认首页
     */
    public static final String INDEX_PAGE = "index.jsp"

    /**
     * 登陆页面
     */
    public static final String LOGIN_PAGE = "/login.jsp"

    /**
     * 跳转URL参数名称
     */
    public static final String REDIRECT_URL = "redirectUrl"

    /**
     * 网站cookies前缀
     */
    public static final String COOKIE_KEY = "eboss"

    public static final String COOKIE_LOGIN_NAME = COOKIE_KEY + "_LOGIN_USERNAME"

    public static final String COOKIE_LOGIN_PASSWORD = COOKIE_KEY + "_LOGIN_PASSWORD"

    public static final String COOKIE_LOGIN_USER_TYPE = COOKIE_KEY + "_LOGIN_USER_TYPE"

    public static final String COOKIE_LOGIN_REFRESH = COOKIE_KEY + "_LOGIN_REFRESH"

    /**
     * 用户cookie中的自动登陆Key
     */
    public static final String COOKIE_AUTO_LOGIN_KEY = COOKIE_KEY + "_KEY"

    /**
     * 用户cookie中的自动登陆用户主键
     */
    public static final String COOKIE_AUTO_LOGIN_USER_ID = COOKIE_KEY + "_USER_ID"

    /**
     * 用户cookie中的自动登陆用户类型
     */
    public static final String COOKIE_AUTO_LOGIN_USER_TYPE = COOKIE_KEY + "_USER_TYPE"

    /**
     * 关注类型：默认
     */
    public static final int CONTACT_TYPE_DEFAULT = 1

    /**
     * 来源：内部资源
     */
    public static final int ATTACH_SOURCE_INTERNAL = 0

    /**
     * 来源：外部资源
     */
    public static final int ATTACH_SOURCE_EXTERNAL = 1

    /**
     * 附件分类：封面
     */
    public static final int ATTACH_TYPE_BOOK_FACE = 0

    /**
     * 附件分类：用户头像
     */
    public static final int ATTACH_TYPE_USER_FACE = 2

    /**
     * 附件分类：小说内容资源
     */
    public static final int ATTACH_TYPE_BOOK_RESOURCE = 1

    /**
     * 自动表别名
     */
    public static final String AUTO_TABLE = "auto_table"

    /**
     * 附件服务器地址
     */
    public static final String ATTACHMENT_SERVER = "/img/"

    /**
     * 按最近加入排序
     */
    public static final String USER_SORT_NEW = "new"

    /**
     * 中转服务器Key
     */
    public static final String CHANNEL_SERVER = "server"

    /**
     * 服务器存储图片的主路径
     */
    public static final String DOMAIN_PATH = "domainPath"

    /**
     * 本地文件存储图片的主路径
     */
    public static final String LOCAL_PATH = "localPath"

    public static final String AUTH_MIDDLEWARE = "ralasafe"

    public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR"

    public static final String SYSTEM_ERROR_MESSAGE = "系统异常，请联系管理员"
}
