package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 重命名节点请求类
 * @date 2024/8/9 下午5:23
 */

@Data
public class RenameNodeVO {
    int id;
    @Length(min = 1, max = 10)
    String node;
    @Pattern(regexp = "(cn|hk|us|jp|de|kr|sg)")
    String location;
}
