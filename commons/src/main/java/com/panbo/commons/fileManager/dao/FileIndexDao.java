package com.panbo.commons.fileManager.dao;

import com.panbo.commons.fileManager.entity.FileIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * @author PanBo 2020/7/23 19:16
 */
public interface FileIndexDao extends CrudRepository<FileIndex, Long> {
    FileIndex findByVirtualName(String virtualName);
    long countByFileId(String fileId);
    Page<FileIndex> findByFileNameIsLikeOrderByCreateDateDesc(String keyword, Pageable pageable);
}
