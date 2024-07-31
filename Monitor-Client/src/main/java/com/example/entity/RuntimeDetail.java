package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ll
 * @description: 运行时的参数实体类
 * @date 2024/7/31 上午11:01
 */
@Data
@Accessors(chain = true)
public class RuntimeDetail {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUpload;
    double networkDownload;
    double diskRead;
    double diskWrite;
}
