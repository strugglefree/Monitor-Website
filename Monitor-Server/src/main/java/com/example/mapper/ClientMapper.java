package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Client;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 客户端数据库映射器
 * @date 2024/7/27 下午3:15
 */
@Mapper
public interface ClientMapper extends BaseMapper<Client> {
}
