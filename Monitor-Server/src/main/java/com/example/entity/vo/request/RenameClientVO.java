package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.NotNull;

/**
 * @author Ll
 * @description: 重命名客户端请求类
 * @date 2024/8/3 下午12:39
 */
@Data
public class RenameClientVO {

    @NotNull
    private int id;
    @Length(min = 1 , max = 10)
    private String name;
}
