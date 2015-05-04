package org.simple.base.nutz.util

import org.nutz.lang.random.R
import org.nutz.lang.random.StringGenerator
import org.simple.base.exception.ServiceException
import org.simple.base.util.AssertUtil
import org.simple.base.util.StringUtil

import javax.servlet.http.HttpSession

public class TokenUtil {

    /**
     * 默认token长度
     */
    public static final int DEFAULT_TOKEN_LENGTH = 6

    public static final String CACHE_KEY = "TOKEN_CACHE"

    public static final int MAX_LOOP = 100000

    /**
     * @param itemLength 随机字符串长度
     * @param size       需要随机的数量
     */
    public static List<String> randomStringWithUnique(int itemLength, int size) {
        int i = 0

        StringGenerator sg = R.sg(itemLength)
        List<String> result = new ArrayList()

        while (result.size() < size) {
            String next = sg.next()

            // 去除重复记录
            if (result.contains(next)) {
                i++
                AssertUtil.assertTrue(i < MAX_LOOP, new ServiceException("无法生成指定数量的随机数"))

                continue
            }

            result.add(next)
        }

        return result
    }

    /**
     * @param itemLength 随机数字长度
     * @param size       需要随机的数量
     */
    public static List<Integer> randomNumberWithUnique(int itemLength, int size) {
        int i = 0

        Integer min = Integer.valueOf("1" + StringUtil.padRight("0", itemLength - 1))
        Integer max = Integer.valueOf(StringUtil.padRight("9", itemLength))

        List<Integer> result = new ArrayList()

        while (result.size() < size) {
            Integer next = R.random(min, max)

            // 去除重复记录
            if (result.contains(next)) {
                i++
                AssertUtil.assertTrue(i < MAX_LOOP, new RuntimeException("无法生成指定数量的随机数"))

                continue
            }

            result.add(next)
        }

        return result
    }

    /**
     * 随机一个长度为itemLength的数字
     *
     * @param itemLength 数字长度
     * @return 随机数字
     */
    public static Integer randomNumber(int itemLength) {
        Integer min = Integer.valueOf("1" + StringUtil.padRight("0", itemLength - 1))
        Integer max = Integer.valueOf(StringUtil.padRight("9", itemLength))
        return R.random(min, max)
    }

    /**
     * 创建token
     */
    public static String createToken(int itemLength) {
        return randomNumber(itemLength) + ""
    }

    public static String createToken() {
        return createToken(DEFAULT_TOKEN_LENGTH)
    }

    /**
     * 为某类key设置token
     */
    public static String put(HttpSession session, String key) {
        String token = createToken()
        session.setAttribute(key, token)
        return token
    }

    /**
     * 为某类key设置token
     */
    public static String put(HttpSession session, String key, int itemLength) {
        String token = createToken(itemLength)
        session.setAttribute(key, token)
        return token
    }

    /**
     * 获取某类key的token
     */
    public static String get(HttpSession session, String key) {
        return (String) session.getAttribute(key)
    }

    /**
     * 某类key验证token
     */
    public static boolean verify(HttpSession session, String key, String value) {
        boolean ok = StringUtil.equals(get(session, key), value)

        if (ok) {
            removeToken(session, key)
        }

        return ok
    }

    /**
     * 删除指定token
     */
    public static void removeToken(HttpSession session, String key) {
        session.removeAttribute(key)
    }
}
