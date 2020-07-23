package com.panbo.commons.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author PanBo 2020/7/23 20:24
 */
@Slf4j
public class ResponseUtil {
    public static void writeMsg(HttpServletResponse response, String msg){
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try(PrintWriter pw = response.getWriter()){
            pw.print(msg);
            pw.flush();
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }
    public static void writeJSON(HttpServletResponse response, int code, String msg){
        writeMsg(response, JsonUtil.writeResponseMsg(code,msg).toJSONString());
    }
}
