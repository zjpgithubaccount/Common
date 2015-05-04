package org.simple.base.nutz.entity

import com.alibaba.fastjson.annotation.JSONField
import org.nutz.dao.Cnd
import org.nutz.dao.entity.annotation.ColDefine
import org.nutz.dao.entity.annotation.ColType
import org.nutz.dao.entity.annotation.Column
import org.nutz.dao.entity.annotation.Comment
import org.nutz.dao.entity.annotation.Name
import org.nutz.dao.pager.Pager
import org.nutz.json.JsonField
import org.nutz.lang.Mirror
import org.simple.base.model.QuickMap
import org.simple.base.nutz.dao.CommonDao
import org.simple.base.nutz.util.IocUtil
import org.simple.base.serializable.SerializableHelper
import org.simple.base.util.ClassUtil

class CommonEntity implements Serializable {

    @Column("create_time")
    @Comment("创建时间")
    @ColDefine(type = ColType.DATETIME)
    Date createTime

    @Column("update_time")
    @ColDefine(type = ColType.DATETIME)
    @Comment("编辑时间")
    Date updateTime

    /**
     * 额外的属性map
     */
    @JsonField(ignore = true)
    @JSONField(serialize = false)
    private QuickMap attributes = new QuickMap()

    String initUUID() {
        String u = UUID.randomUUID().toString().replace('-', '').toUpperCase()

        for (it in this.class.declaredFields) {
            it.setAccessible(true)
            if (it.getAnnotation(Name)) {
                return it.get(this) ?: u
            }
        }

        return u
    }

    void setAttributes(QuickMap attributes) {
        this.attributes = attributes
    }

    /**
     * 初始化属性map
     */
    void attr(QuickMap attributes) {
        this.attributes.putAll(attributes)
    }

    /**
     * 为属性map赋值
     *
     * @param key 键
     * @param value 值
     */
    void attr(String key, Object value) {
        attributes.put(key, value)
    }

    /**
     * 获取属性map中的值
     */
    public <T> T attr(key) {
        return (T) attributes[key]
    }

    static CommonDao dao() {
        return IocUtil.dao()
    }

    void save() {
        dao().save(this)
    }

    public <T> T insert() {
        return (T) dao().insert(this)
    }

    void fastInsert() {
        dao().fastInsert(this)
    }

    public <T> T insert(String active) {
        return (T) dao().insert(this, active)
    }

    int update() {
        return dao().update(this)
    }

    void update(String active) {
        dao().update(this, active)
    }

    public <T> List<T> query(Cnd cnd, Pager pager = null) {
        return (List<T>) dao().query(this.class, cnd, pager)
    }

    public <T> T fetchLinks(String regex) {
        return (T) dao().fetchLinks(this, regex)
    }

    @SuppressWarnings("unchecked")
    <T extends CommonEntity> T fastCopy() {
        T newEntity = (T) Mirror.me(this).born()
        ClassUtil.copySameProperties(newEntity, this)
        return newEntity
    }

    @SuppressWarnings("unchecked")
    <T extends CommonEntity> T copy() {
        return (T) SerializableHelper.clone(this)
    }

    @JsonField(ignore = true)
    @JSONField(serialize = false)
    String getTableName() {
        return dao().getEntity(this.class).getTableName()
    }
}