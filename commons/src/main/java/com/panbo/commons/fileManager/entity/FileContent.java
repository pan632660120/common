package com.panbo.commons.fileManager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

/**
 * @author PanBo 2020/7/22 16:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FileContent {
    @Id
    private String id;
    private String gridFSId;//大文件GridFS的ID
    private byte[] content;//文件内容
    private String MD5;//文件MD5
    private boolean isGridFS;//是否存储于GridFS
    private long size;//文件大小
    private transient String fileName;//文件名
}
