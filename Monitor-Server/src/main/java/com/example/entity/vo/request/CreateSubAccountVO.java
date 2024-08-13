package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author Ll
 * @description: 创建子账户请求实体类
 * @date 2024/8/13 下午12:28
 */
@Data
public class CreateSubAccountVO {
    @Length(min = 1, max = 10)
    String username;
    @Email
    String email;
    @Length(min = 6, max = 16)
    String password;
    @Size(min = 1)
    List<Integer> clients;
}
