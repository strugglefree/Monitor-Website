package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ClientDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 客户端详细信息映射器
 * @date 2024/7/30 下午4:06
 */
@Mapper
public interface ClientDetailMapper extends BaseMapper<ClientDetail> {
}
