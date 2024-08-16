package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: ssh设置响应实体类
 * @date 2024/8/16 下午3:27
 */
@Data
public class SshSettingVO {

    private String ip;
    private int port = 22;
    private String username;
    private String password;
}
