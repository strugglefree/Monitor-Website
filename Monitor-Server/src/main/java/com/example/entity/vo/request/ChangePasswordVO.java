package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 更改密码的请求实体类
 * @date 2024/8/13 上午11:18
 */
@Data
public class ChangePasswordVO {
    @Length(min = 6, max = 16)
    String password;
    @Length(min = 6, max = 16)
    String new_password;
}
