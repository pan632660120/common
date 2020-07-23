package com.panbo.commons.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author PanBo 2020/7/23 20:15
 */
public class JsonUtil {
    public static JSONObject writeResponseMsg(JSONObject json, int code, String msg){
        json.put("code", code);
        json.put("message", msg);
        return json;
    }
    public static JSONObject writeResponseMsg(int code, String msg){
        JSONObject json = new JSONObject();
        return writeResponseMsg(json,code,msg);
    }
}
