package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ll
 * @description: 运行时数据的响应实体类
 * @date 2024/8/11 上午7:16
 */
@Data
public class RuntimeDetailsVO {
    double disk;
    double memory;
    List<JSONObject> list = new LinkedList<>();
}
