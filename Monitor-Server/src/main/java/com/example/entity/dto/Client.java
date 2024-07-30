package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 客户端类
 * @date 2024/7/27 下午3:13
 */
@Data
@TableName("db_client")
@AllArgsConstructor
public class Client {
    @TableId
    Integer id;
    String name;
    String token;
    String location;
    String node;
    Date registerTime;
}
