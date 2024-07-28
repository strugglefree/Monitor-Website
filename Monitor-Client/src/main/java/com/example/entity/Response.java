package com.example.entity;

import com.alibaba.fastjson2.JSONObject;

/**
 * @description: 响应实体类
 * @author Ll
 * @date: 2024/7/28 下午2:53
 */
public record Response(int id, int code, Object data, String message) {

    /**
     * @description: 判断响应是否成功
     * @param: []
     * @return: boolean
     * @author Ll
     * @date: 2024/7/28 下午2:55
     */
    public boolean success(){
        return code == 200;
    }

    /**
     * @description: 数据转JSON
     * @param: []
     * @return: com.alibaba.fastjson2.JSONObject
     * @author Ll
     * @date: 2024/7/28 下午2:55
     */
    public JSONObject asJson(){
        return JSONObject.from(data);
    }

    /**
     * @description: 把数据转成String
     * @param: []
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/28 下午2:56
     */
    public String asString(){
        return data.toString();
    }

    /**
     * @description: 错误响应
     * @param: [e]
     * @return: com.example.entity.Response
     * @author Ll
     * @date: 2024/7/28 下午3:02
     */
    public static Response errorResponse(Exception e){
        return new Response(0, 500, null, e.getMessage());
    }
}
