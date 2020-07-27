package com.panbo.commons.entity;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Request包装类
 * @author PanBo 2020/7/27 11:15
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;
    private int length;
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            body = IoUtil.readBytes(request.getInputStream());
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
        length = request.getContentLength();
    }
    public ServletInputStream getInputStream(){
        InputStream in = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return in.read();
            }
        };
    }

    public String getContent(){
        return length > 0 ? new String(body, 0, length, StandardCharsets.UTF_8) : "";
    }

    public int getLength() {
        return length;
    }
}
