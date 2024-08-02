package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: 客户端信息响应类
 * @date 2024/8/2 下午3:41
 */
@Data
public class ClientPreviewVO {
    int id;
    boolean online;
    String name;
    String location;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double memory;
    double cpuUsage;
    double memoryUsage;
    double networkUpload;
    double networkDownload;
}
