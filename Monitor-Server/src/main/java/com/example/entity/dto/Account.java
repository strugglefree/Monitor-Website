package com.example.entity.dto;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 数据库中的用户信息
 */
@Data
@TableName("db_account")
@AllArgsConstructor
public class Account implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String email;
    String role;
    String clients;
    Date registerTime;

    /**
     * @description: 获取其下所有管理的客户端ID
     * @param: []
     * @return: java.util.List<java.lang.Integer>
     * @author Ll
     * @date: 2024/8/14 下午1:57
     */
    public List<Integer> getClientList(){
        if(clients == null){ return Collections.emptyList(); }
        return JSONArray.parseArray(clients).toList(Integer.class);
    }
}
