package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zl
 * @email fengge@atguigu.com
 * @date 2020-10-28 19:03:04
 */
@Mapper
public interface CouponMapper extends BaseMapper<CouponEntity> {
	
}
