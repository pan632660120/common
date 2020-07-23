package com.panbo.commons.fileManager.dao;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.panbo.commons.fileManager.entity.FileContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

/**
 * @author PanBo 2020/7/23 10:30
 */
@Repository
@Slf4j
public class FileContentDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 存储FileContent
     * @param fileContent
     * @return FileContent的id
     */
    public String storeFileContent(FileContent fileContent){
        return mongoTemplate.save(fileContent).getId();
    }

    public String storeFile2GridFS(InputStream in, String contentType){
        String gridFSId = IdUtil.simpleUUID();
        gridFsTemplate.store(in, gridFSId, contentType);
        return gridFSId;
    }
    public FileContent getByMD5(String MD5){
        Query query = new Query().addCriteria(Criteria.where("MD5").is(MD5));
        return mongoTemplate.findOne(query, FileContent.class);
    }
    public FileContent getById(String id){
        return mongoTemplate.findById(id, FileContent.class);
    }
    public void readContentFromGridFS(FileContent fileContent){
        if(fileContent.isGridFS()){
            Query query = new Query().addCriteria(GridFsCriteria.whereFilename().is(fileContent.getGridFSId()));
            try {
                GridFSFile gridFSFile = gridFsTemplate.findOne(query);
                GridFSDownloadStream in = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                GridFsResource resource = new GridFsResource(gridFSFile, in);
                fileContent.setContent(IoUtil.readBytes(resource.getInputStream()));
            }catch (Exception e){
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }
    }
    public void delFileFromGridFS(String gridFSId){
        Query query = new Query().addCriteria(GridFsCriteria.whereFilename().is(gridFSId));
        gridFsTemplate.delete(query);
    }
    public void delFileContent(FileContent fileContent){
        mongoTemplate.remove(fileContent);
    }
    public void delFileContentByFileId(String fileId){
        Query query = new Query().addCriteria(Criteria.where("_id").is(fileId));
        mongoTemplate.findAndRemove(query, FileContent.class);
    }
}
