package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: ssh链接所需的请求实体类
 * @date 2024/8/16 下午3:17
 */
@Data
public class SshConnectionVO {
   int id;
   int port;
   @NotNull
   @Length(min = 1)
   String username;
   @NotNull
   @Length(min = 1)
   String password;
}
