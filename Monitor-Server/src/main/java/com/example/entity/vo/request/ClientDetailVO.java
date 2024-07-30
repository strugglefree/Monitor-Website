package com.example.entity.vo.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * @author Ll
 * @description: 客户端的主机信息请求类
 * @date 2024/7/30 下午3:59
 */
@Data
public class ClientDetailVO {
    @NotNull
    String osArch;
    @NotNull
    String osName;
    @NotNull
    String osVersion;
    @NotNull
    int osBit;
    @NotNull
    String cpuName;
    @NotNull
    int cpuCore;
    @NotNull
    double memory;
    @NotNull
    double disk;
    @NotNull
    String ip;
}
