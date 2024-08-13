package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: 客户端响应实体类的简单信息
 * @date 2024/8/13 下午3:22
 */
@Data
public class ClientSimpleVO {
    int id;
    String name;
    String location;
    String osName;
    String osVersion;
    String ip;
}
