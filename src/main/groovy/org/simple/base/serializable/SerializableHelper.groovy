package org.simple.base.serializable

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer
import com.thoughtworks.xstream.io.xml.XppDriver

/**
 *
 * Date: 12-2-24
 * @author Jay Wu
 *
 */
class SerializableHelper {
    static DEFAULT_CHARTSET = "UTF-8"

    static XStream getX() {
        XStream x = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")))
        return x
    }

    private static init(XStream x, Class clazz) {
        if (clazz) {
            x.processAnnotations(clazz);
//            x.aliasPackage("", clazz.package.name);
        }
    }

    private static String to(XStream x, def obj) {
        return new String(x.toXML(obj).bytes, DEFAULT_CHARTSET)
    }

    private static <T> T from(XStream x, String content) {
        return (T) x.fromXML(new String(content.getBytes(DEFAULT_CHARTSET)))
    }

    private static <T> T from(XStream x, String content, def obj) {
        return (T) x.fromXML(new String(content.getBytes(DEFAULT_CHARTSET)), obj)
    }

    static String toXML(def obj) {
        XStream x = getX()
        init(x, obj.class)
        return to(x, obj)
    }

    static <T> T fromXML(String content, Class clazz = null) {
        XStream x = getX()
        init(x, clazz)
        return from(x, content) as T
    }

    static <T> T appendFromXML(String content, Object obj) {
        XStream x = getX()
        init(x, obj.class)
        return from(x, content, obj) as T
    }

    static String toJSON(def obj) {
        String text = JSON.toJSONString(obj, SerializerFeature.WriteClassName);
        return new String(text.bytes, DEFAULT_CHARTSET)
    }

    /**
     * 利用序列化然后再反序列化克隆对象
     *
     * @param obj
     * @return
     */
    static <T> T clone(T obj) {
        return fromJSON(toJSON(obj), obj.getClass()) as T
    }

    static <T> T fromJSON(String content, Class clazz = null) {
        content = new String(content.getBytes(DEFAULT_CHARTSET))
        if (clazz) {
            return JSON.parseObject(content, clazz) as T;
        } else {
            return JSON.parse(content) as T
        }
    }
}