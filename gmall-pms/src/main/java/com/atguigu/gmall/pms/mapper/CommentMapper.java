package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价
 * 
 * @author zl
 * @email fengge@atguigu.com
 * @date 2020-10-28 16:26:42
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
	
}
