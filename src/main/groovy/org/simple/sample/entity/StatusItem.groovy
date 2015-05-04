package org.simple.sample.entity

import org.nutz.dao.entity.annotation.ColDefine
import org.nutz.dao.entity.annotation.ColType
import org.nutz.dao.entity.annotation.Column
import org.nutz.dao.entity.annotation.Comment
import org.nutz.dao.entity.annotation.Name
import org.nutz.dao.entity.annotation.Table
import org.simple.base.nutz.entity.OperationEntity

@Table("status_item")
@Comment("状态项")
public class StatusItem extends OperationEntity {

    /**
     * 状态标识
     */
    @Name
    @Column("status_id")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("状态标识")
    String id;
    /**
     * 状态类型标识
     */
    @Column("status_type_id")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("状态类型标识")
    String statusTypeId;
    /**
     * 状态编码
     */
    @Column("status_code")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("状态编码")
    String statusCode;
    /**
     * 序列标识
     */
    @Column("sequence_id")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    @Comment("序列标识")
    String sequenceId;
    /**
     * 描述
     */
    @Column("description")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @Comment("描述")
    String description;
}
