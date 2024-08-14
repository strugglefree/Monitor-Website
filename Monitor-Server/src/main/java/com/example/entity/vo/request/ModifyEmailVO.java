package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 修改电子邮件请求实体类
 * @date 2024/8/14 下午3:41
 */
@Data
public class ModifyEmailVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
}
