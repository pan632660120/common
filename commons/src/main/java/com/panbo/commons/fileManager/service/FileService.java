package com.panbo.commons.fileManager.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.panbo.commons.fileManager.dao.FileContentDao;
import com.panbo.commons.fileManager.dao.FileIndexDao;
import com.panbo.commons.fileManager.entity.FileContent;
import com.panbo.commons.fileManager.entity.FileIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

/**
 * @author PanBo 2020/7/23 19:19
 */
@Service
@Slf4j
public class FileService {
    @Autowired
    private FileContentDao fileContentDao;
    @Autowired
    private FileIndexDao fileIndexDao;

    @Transactional
    public String fileUpload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String md5 = SecureUtil.md5(file.getInputStream());
        int index = fileName.lastIndexOf(".");
        String suffix = index >= 0 ? fileName.substring(index) : "";
        String virtualName = IdUtil.simpleUUID() + suffix;
        FileContent fileContent = fileContentDao.getByMD5(md5);
        FileIndex fi = new FileIndex();
        fi.setVirtualName(virtualName);
        fi.setFileName(fileName);
        fi.setDownURL("file/downFile/" + virtualName);
        if(fileContent != null){//文件已存在，秒传，保存索引即可
            fi.setFileId(fileContent.getId());
        }else {//文件不存在
            fileContent = new FileContent();
            fileContent.setMD5(md5);
            fileContent.setSize(file.getSize());
            if(file.getSize() > 1024 * 1024 * 15){
                String gridFSId = fileContentDao.storeFile2GridFS(file.getInputStream(), file.getContentType());
                fileContent.setGridFS(true);
                fileContent.setGridFSId(gridFSId);
            }else {
                fileContent.setContent(file.getBytes());
                fileContent.setGridFS(false);
            }
            String newFileId = fileContentDao.storeFileContent(fileContent);
            fi.setFileId(newFileId);
        }
        fileIndexDao.save(fi);
        return virtualName;
    }

    public FileContent getFileByVirtualName(String virtualName){
        FileIndex fileIndex = fileIndexDao.findByVirtualName(virtualName);
        return getFileContent(fileIndex);
    }
    public FileContent getFileById(long id){
        FileIndex fileIndex = fileIndexDao.findById(id).get();
        return getFileContent(fileIndex);
    }
    public FileContent getFileContent(FileIndex fileIndex){
        if(fileIndex != null){
            FileContent fileContent = fileContentDao.getById(fileIndex.getFileId());
            if(fileContent != null){
                if(fileContent.isGridFS()){
                    fileContentDao.readContentFromGridFS(fileContent);
                }
                fileContent.setFileName(fileIndex.getFileName());
                return fileContent;
            }
        }
        return null;
    }
    private void delFile(FileIndex fileIndex){
        String fileId = fileIndex.getFileId();
        fileIndexDao.delete(fileIndex);
        if(fileIndexDao.countByFileId(fileId) == 0){
            FileContent fileContent = fileContentDao.getById(fileId);
            if(fileContent != null){
                if(fileContent.isGridFS()){
                    String gridFSId = fileContent.getGridFSId();
                    fileContentDao.delFileFromGridFS(gridFSId);
                }
                fileContentDao.delFileContent(fileContent);
            }
        }
    }

    public void delFileById(long id){
        Optional<FileIndex> opt = fileIndexDao.findById(id);
        assert opt.isPresent();
        delFile(opt.get());
    }
    public void delFileByVirtualName(String virtualName){
        FileIndex fileIndex = fileIndexDao.findByVirtualName(virtualName);
        delFile(fileIndex);
    }
    public Page<FileIndex> getFileIndexes(Pageable pageable, String keyword){
        return fileIndexDao.findByFileNameIsLikeOrderByCreateDateDesc(keyword, pageable);
    }
}
