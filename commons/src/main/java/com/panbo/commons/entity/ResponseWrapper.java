package com.panbo.commons.entity;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author PanBo 2020/7/27 11:22
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
