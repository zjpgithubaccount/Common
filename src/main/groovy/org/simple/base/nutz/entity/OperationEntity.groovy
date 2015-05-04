package org.simple.base.nutz.entity

import org.nutz.dao.entity.annotation.ColDefine
import org.nutz.dao.entity.annotation.ColType
import org.nutz.dao.entity.annotation.Column
import org.nutz.dao.entity.annotation.Comment

public class OperationEntity extends CommonEntity {

    @Column("creator")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("创建者")
    String creator

    @Column("creator_name")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Comment("创建者名称")
    String creatorName

    @Column("editor")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("编辑者")
    String editor

    @Column("editor_name")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Comment("编辑者名称")
    String editorName

    void setOperator(String operatorId, String operatorName, boolean isCreate) {
        if (isCreate) {
            this.creator = operatorId
            this.creatorName = operatorName
        }
        this.editor = operatorId
        this.editorName = operatorName

        createTime = createTime ?: new Date()
        updateTime = updateTime ?: new Date()
    }
}
