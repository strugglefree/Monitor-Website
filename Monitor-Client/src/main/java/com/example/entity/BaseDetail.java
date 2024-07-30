package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ll
 * @description: 服务器基本配置实体类
 * @date 2024/7/30 下午2:41
 */
@Data
@Accessors(chain = true)
public class BaseDetail {
    String osArch;
    String osName;
    String osVersion;
    int osBit;
    String cpuName;
    int cpuCore;
    double memory;
    double disk;
    String ip;
}
