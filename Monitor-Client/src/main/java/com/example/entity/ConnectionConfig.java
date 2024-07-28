package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ll
 * @description: 链接配置实体类
 * @date 2024/7/28 下午2:27
 */
@Data
@AllArgsConstructor
public class ConnectionConfig {
    String address;
    String token;
}
