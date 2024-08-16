package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ClientSsh;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 客户端ssh连接mapper
 * @date 2024/8/16 下午3:04
 */
@Mapper
public interface ClientSshMapper extends BaseMapper<ClientSsh> {
}
