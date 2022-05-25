package com.itheima.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表(Orders)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-23 14:56:05
 */
@Mapper
public interface OrdersDao extends BaseMapper<Orders> {


}

