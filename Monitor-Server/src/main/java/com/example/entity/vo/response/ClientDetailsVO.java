package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: 客户端详细信息响应类
 * @date 2024/8/3 下午3:14
 */
@Data
public class ClientDetailsVO {
    int id;
    boolean online;
    String name;
    String node;
    String location;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double memory;
    double disk;
}
