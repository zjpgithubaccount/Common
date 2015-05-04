package org.simple.base.json.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import groovy.json.JsonBuilder
import groovy.json.JsonLexer
import groovy.json.JsonToken
import groovy.json.JsonTokenType
import groovy.transform.CompileStatic
import org.nutz.json.Json
import org.nutz.json.JsonFormat
import org.nutz.lang.Strings
import org.nutz.log.Log
import org.nutz.log.Logs

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author zhangjp 2015-04-29
 */
@CompileStatic
public class JsonUtil {

    private static final transient Log log = Logs.get()

    static enum JsonLib {
        NUTZ,
        FAST_JSON
    }

    public static String dataFormat = "yyyy-MM-dd HH:mm:ss"

    public static JsonLib USE_LIB = JsonLib.FAST_JSON

    public static String toJson(Object obj) {
        return toJson(obj, false)
    }

    public static String toJson(Object obj, boolean pretty) {
        if (obj == null) {
            return null
        }

        if (obj instanceof String) {
            return obj
        }

        switch (USE_LIB) {
            case JsonLib.NUTZ:
                if (pretty) {
                    return Json.toJson(obj)
                } else {
                    return Json.toJson(obj, JsonFormat.compact())
                }

            case JsonLib.FAST_JSON:
                def features = [SerializerFeature.DisableCircularReferenceDetect]

                if (pretty) {
                    features << SerializerFeature.PrettyFormat
                }

                return JSON.toJSONStringWithDateFormat(obj, dataFormat, features as SerializerFeature[])
        }

        return null
    }

    /**
     * 用于序列化对象的json文本
     */
    public static String serialize(Object o) {
        return JSON.toJSONString(o, SerializerFeature.WriteClassName)
    }

    public static Object fromJson(String json) {
        if (Strings.isBlank(json)) {
            return null
        }

        switch (USE_LIB) {
            case JsonLib.NUTZ:
                return Json.fromJson(json)

            case JsonLib.FAST_JSON:
                return JSON.parse(json)
        }

        return null
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        if (Strings.isBlank(json)) {
            return null
        }

        switch (USE_LIB) {
            case JsonLib.NUTZ:
                return Json.fromJson(clazz, json)

            case JsonLib.FAST_JSON:
                return JSON.parseObject(json, clazz)
        }

        return null
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(Type type, String json) {
        if (Strings.isBlank(json)) {
            return null
        }

        switch (USE_LIB) {
            case JsonLib.NUTZ:
                return (T) Json.fromJson(type, json)

            case JsonLib.FAST_JSON:
                return JSON.parseObject(json, type)
        }

        return null
    }

    public static <T> T fromJson(TypeReference type, String json) {
        return fromJson(type.getType(), json)
    }

    static <T> T build(Closure source, Class<T> clazz) {
        JsonBuilder builder = new JsonBuilder()
        builder.call(source)
        return fromJson(clazz, builder.toString())
    }

    public static class TypeReference<T> {

        private final Type type

        protected TypeReference() {
            Type superClass = getClass().getGenericSuperclass()

            type = ((ParameterizedType) superClass).getActualTypeArguments()[0]
        }

        public Type getType() {
            return type
        }

        public final static Type LIST_STRING = new TypeReference<List<String>>() {
        }.getType()
    }

    static String prettyPrint(String jsonPayload) {
        if (!jsonPayload) {
            return ""
        }

        try {
            int indent = 0
            def output = new StringBuilder()
            def lexer = new JsonLexer(new StringReader(jsonPayload))

            while (lexer.hasNext()) {
                JsonToken token = lexer.next()
                if (token.type == JsonTokenType.OPEN_CURLY) {
                    indent += 4
                    output.append('{\n')
                    output.append(' ' * indent)
                } else if (token.type == JsonTokenType.CLOSE_CURLY) {
                    indent -= 4
                    output.append('\n')
                    output.append(' ' * indent)
                    output.append('}')
                } else if (token.type == JsonTokenType.OPEN_BRACKET) {
                    indent += 4
                    output.append('[\n')
                    output.append(' ' * indent)
                } else if (token.type == JsonTokenType.CLOSE_BRACKET) {
                    indent -= 4
                    output.append('\n')
                    output.append(' ' * indent)
                    output.append(']')
                } else if (token.type == JsonTokenType.COMMA) {
                    output.append(',\n')
                    output.append(' ' * indent)
                } else if (token.type == JsonTokenType.COLON) {
                    output.append(': ')
                } else if (token.type == JsonTokenType.STRING) {
                    output.append('"' + token.text[1..-2] + '"')
                } else {
                    output.append(token.text)
                }
            }

            return output.toString()
        } catch (Throwable e) {
            log.error("prettyPrint error: " + e.message)
        }

        return jsonPayload
    }
}
