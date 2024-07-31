package com.example.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

/**
 * @author Ll
 * @description: influx存储的数据
 * @date 2024/7/31 下午2:58
 */
@Data
@Measurement(name = "runtime")
public class RuntimeData {
    @Column(tag = true)
    int clientId;
    @Column(timestamp = true)
    Instant timestamp;
    @Column
    double cpuUsage;
    @Column
    double memoryUsage;
    @Column
    double diskUsage;
    @Column
    double networkUpload;
    @Column
    double networkDownload;
    @Column
    double diskRead;
    @Column
    double diskWrite;
}
