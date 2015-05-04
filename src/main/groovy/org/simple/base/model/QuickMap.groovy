package org.simple.base.model

import org.nutz.castor.Castors
import org.nutz.dao.DaoException
import org.nutz.lang.Lang
import org.nutz.lang.Strings
import org.nutz.mapl.Mapl
import org.simple.base.json.util.JsonUtil

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Timestamp
import java.sql.Types

public class QuickMap extends LinkedHashMap<String, Object> {

    public int getInt(String name) {
        return Castors.me().castTo(get(name), Integer.class)
    }

    public String getString(String name) {
        return Castors.me().castToString(get(name))
    }

    public Timestamp getTimestamp(String name) {
        return Castors.me().castTo(get(name), Timestamp.class)
    }

    public Boolean getBoolean(String name) {
        return Castors.me().castTo(get(name), Boolean.class)
    }

    public String toJson() {
        return JsonUtil.toJson(this, true)
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) super.get(key)
    }

    public String toString() {
        return toJson()
    }

    /**
     * 根据一段字符串，生成一个 Map 对象。
     *
     * @param str 参照 JSON 标准的字符串，但是可以没有前后的大括号
     * @return Map 对象
     */
    public static QuickMap map(String str) {
        QuickMap map = new QuickMap()
        map.putAll(Lang.map(str))
        return map
    }

    @Override
    public QuickMap put(String key, Object value) {
        super.put(key, value)
        return this
    }

    /**
     * 将key自动装错驼峰写法
     *
     * @param map
     */
    public void putAllWithCamel(Map<?, ?> map) {
        for (Map.Entry entry : map.entrySet()) {
            put(Strings.upperWord(entry.getKey().toString(), '_' as char), entry.getValue())
        }
    }

    /**
     * 将其他类型的map转成QuickMap
     *
     * @param list
     * @param camel 是否自动将Key转换成驼峰类型
     * @return
     */
    public static List<QuickMap> maps(Collection<? extends Map<? extends String, ?>> list, boolean camel) {
        List<QuickMap> newList = new ArrayList<QuickMap>()

        for (Map<? extends String, ?> m : list) {
            QuickMap quickMap = new QuickMap()

            if (camel) {
                quickMap.putAllWithCamel(m)
            } else {
                quickMap.putAll(m)
            }

            newList.add(quickMap)
        }

        return newList
    }

    /**
     * 根据路径访问指定的值
     * <p/>
     * <code>
     * QuickMap map = [a: [b: [c: 1]]]
     * assert map.cell("a.b.c") == 1
     * </code>
     */
    @SuppressWarnings("unchecked")
    public <T> T cell(String key) {
        return (T) Mapl.cell(this, key)
    }

    public static QuickMap fromObject(Object obj) {
        QuickMap map = Lang.obj2map(obj, QuickMap.class)
        map.remove("metaClass")

        return map
    }

    public <T> T toObject(Class<T> clazz) {
        return Lang.map2Object(this, clazz)
    }

    /**
     * 将XPath格式转换为对应的Map对象
     * <p/>
     * <code>
     * ["a.b.c": "c1", "a.d[1]": "d1","a.d[0]": "d0","a.e[0].id": "eId1","a.e[1].id": "eId2"]
     * ==
     * [a:[b:[c:c1], d:[d0, d1], e:[[id:eId1], [id:eId2]]]]
     * </code>
     */
    public static QuickMap fromPaths(Map<String, Object> paths) {
        QuickMap result = new QuickMap()

        if (paths == null) {
            return result
        }

        for (String path : paths.keySet()) {
            String[] keys = path.split("\\.")
            QuickMap now = result

            for (String key : keys) {
                // 存在数组
                if (key.contains("[")) {
                    // 获取真正的key
                    String pKey = key.substring(0, key.indexOf("["))
                    List<Object> list = now.get(pKey)

                    if (list == null) {
                        list = new ArrayList<Object>()
                        now.put(pKey, list)
                    }

                    // 获取数组索引
                    int index = Integer.valueOf(key.substring(key.indexOf("[") + 1, key.lastIndexOf("]")))

                    for (int i = list.size() - 1; i < index; i++) {
                        list.add(null)
                    }

                    // 到达叶子结点
                    if (path.endsWith(key)) {
                        if (list.get(index) == null) {
                            list.set(index, paths.get(path))
                        }
                    } else {
                        // 非叶子结点
                        now = (QuickMap) list.get(index)

                        if (now == null) {
                            new QuickMap()
                            list.set(index, now)
                        }
                    }
                } else {
                    // 到达叶子结点
                    if (path.endsWith(key)) {
                        now.put(key, paths.get(path))
                    } else {
                        // 非叶子结点
                        QuickMap temp = now.get(key)

                        if (temp == null) {
                            temp = new QuickMap()
                            now.put(key, temp)
                        }

                        now = temp
                    }
                }
            }
        }

        return result
    }

    /**
     * 根据XPath获取对应的key
     */
    @SuppressWarnings("unchecked")
    public static Object getFromPath(Map<String, Object> map, String path) {
        if (path == null || map == null) {
            return null
        }

        Map<String, Object> now = map
        for (String key : path.split("\\.")) {
            // 存在数组
            if (key.contains("[")) {
                // 获取真正的key
                String pKey = key.substring(0, key.indexOf("["))
                // 获取数组索引
                int index = Integer.valueOf(key.substring(key.indexOf("[") + 1, key.lastIndexOf("]")))

                List<Object> list = (List<Object>) now.get(pKey)

                if (list == null || index >= list.size()) {
                    return null
                }

                Object ele = list.get(index)

                // 到达叶子结点
                if (path.endsWith(key)) {
                    return ele
                } else {
                    // 非叶子结点
                    now = (Map<String, Object>) ele
                }
            } else {
                Object ele = now.get(key)

                if (ele == null) {
                    return null
                }

                // 到达叶子结点
                if (path.endsWith(key)) {
                    return ele
                } else {
                    // 非叶子结点
                    now = (Map<String, Object>) ele
                }
            }
        }

        return null
    }

    /**
     * 根据 ResultSet 创建一个记录对象
     *
     * @param rs ResultSet 对象
     * @return 记录对象
     */
    public static QuickMap create(ResultSet rs) {
        String name = null
        int i = 0
        try {
            QuickMap re = new QuickMap()
            ResultSetMetaData meta = rs.getMetaData()
            int count = meta.getColumnCount()
            for (i = 1; i <= count; i++) {
                name = Strings.upperWord(meta.getColumnLabel(i).toLowerCase(), '_' as char)

                switch (meta.getColumnType(i)) {
                    case Types.TIMESTAMP:
                        re.put(name, rs.getTimestamp(i))
                        break

                    case Types.DATE: // ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间 from
                        // cqyunqin
                        re.put(name, rs.getTimestamp(i))
                        break

                    case Types.CLOB:
                        re.put(name, rs.getString(i))
                        break

                    default:
                        re.put(name, rs.getObject(i))
                        break
                }
            }
            return re
        } catch (SQLException e) {
            if (name != null) {
                throw new DaoException(String.format("Column Name=%s, index=%d", name, i), e)
            }
            throw new DaoException(e)
        }
    }
}
