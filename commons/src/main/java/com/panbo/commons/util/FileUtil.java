package com.panbo.commons.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author PanBo 2020/7/22 15:55
 */
@Slf4j
public class FileUtil {
    public static void fileDownload(HttpServletResponse response, byte[] content, String fileName){
        response.setContentType("application/octet-stream");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
        response.addHeader("Content-Disposition","attachment;fileName=" + fileName);
        response.addHeader("content-type","application/octet-stream");
        try (OutputStream os = response.getOutputStream()){
            os.write(content);
            os.flush();
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }
}
