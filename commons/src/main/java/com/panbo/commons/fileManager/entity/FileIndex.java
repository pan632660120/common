package com.panbo.commons.fileManager.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panbo.commons.common.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author PanBo 2020/7/22 16:15
 */
@Data
@NoArgsConstructor
@Entity
@Table
public class FileIndex extends BaseEntity {
    private String fileName;//文件实际名字
    @JSONField(serialize = false)
    @JsonIgnore
    @Column(length = 64)
    private String virtualName;//文件唯一名字
    @JSONField(serialize = false)
    @JsonIgnore
    private String fileId;//MongoId
    private Date createDate;
    private String downURL;

    @PrePersist
    public void prePersist(){
        this.createDate = new Date();
    }
}
