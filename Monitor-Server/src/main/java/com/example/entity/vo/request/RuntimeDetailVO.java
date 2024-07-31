package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Ll
 * @description: 请求运行时的数据实体类
 * @date 2024/7/31 下午12:30
 */
@Data
public class RuntimeDetailVO {
    @NotNull
    long timestamp;
    @NotNull
    double cpuUsage;
    @NotNull
    double memoryUsage;
    @NotNull
    double diskUsage;
    @NotNull
    double networkUpload;
    @NotNull
    double networkDownload;
    @NotNull
    double diskRead;
    @NotNull
    double diskWrite;
}
