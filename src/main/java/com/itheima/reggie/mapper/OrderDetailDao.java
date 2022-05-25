package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细表(OrderDetail)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-23 14:56:05
 */
@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {



}

