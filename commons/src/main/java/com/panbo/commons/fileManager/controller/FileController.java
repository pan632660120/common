package com.panbo.commons.fileManager.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.panbo.commons.fileManager.entity.FileContent;
import com.panbo.commons.fileManager.entity.FileIndex;
import com.panbo.commons.fileManager.service.FileService;
import com.panbo.commons.util.FileUtil;
import com.panbo.commons.util.JsonUtil;
import com.panbo.commons.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

/**
 * @author PanBo 2020/7/23 20:12
 */
@RestController
@RequestMapping("file")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("upload")
    public JSONObject fileUpLoad(MultipartFile file){
        String virtualName = "";
        try {
            virtualName = fileService.fileUpload(file);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return JsonUtil.writeResponseMsg(2,"文件上传失败");
        }
        JSONObject json = JsonUtil.writeResponseMsg(0,"文件上传成功");
        json.put("downURL","file/downFile/" + virtualName);
        return json;
    }
    @GetMapping({"downFile/{virtualName}","downFile"})
    public void fileDownload(@PathVariable(required = false) String virtualName, HttpServletResponse response,
                             @RequestParam(defaultValue = "-1") long id){
        FileContent fileContent = null;
        if(virtualName != null){
            fileContent = fileService.getFileByVirtualName(virtualName);
        }else {
            fileContent = fileService.getFileById(id);
        }
        if(fileContent != null){
            FileUtil.fileDownload(response, fileContent.getContent(), fileContent.getFileName());
        }else {
            ResponseUtil.writeJSON(response, 2, "文件不存在");
        }
    }
    @GetMapping({"delFile/{virtualName}","delFile"})
    public JSONObject delFile(@PathVariable(required = false) String virtualName, @RequestParam(defaultValue = "-1") long id){
        try{
            if(virtualName != null){
                fileService.delFileByVirtualName(virtualName);
            }else {
                fileService.delFileById(id);
            }
        }catch (Exception e){
            log.error(ExceptionUtil.stacktraceToString(e));
            return JsonUtil.writeResponseMsg(2, "文件删除失败");
        }
        return JsonUtil.writeResponseMsg(0, "文件删除成功");
    }
    @GetMapping("displayFiles")
    public Stream<FileIndex> displayFiles(@RequestParam(defaultValue = "-1") int pageSize, @RequestParam(defaultValue = "0") int pageNo,
                                          String keyword){
        Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : null;
        keyword = StrUtil.isBlank(keyword) ? "%" : "%" + keyword + "%";
        return fileService.getFileIndexes(pageable, keyword).get();
    }

}
