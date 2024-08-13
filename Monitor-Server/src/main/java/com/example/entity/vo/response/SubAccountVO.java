package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

/**
 * @author Ll
 * @description: 子用户响应实体类
 * @date 2024/8/13 下午12:32
 */
@Data
public class SubAccountVO {
    int id;
    String username;
    String email;
    JSONArray clientList;
}
