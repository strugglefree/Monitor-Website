package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Ll
 * @description: 客户端的主机信息
 * @date 2024/7/30 下午3:55
 */
@Data
@TableName("db_client_detail")
public class ClientDetail {
    @TableId
    Integer id;
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
