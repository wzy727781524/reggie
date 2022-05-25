package com.itheima.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车(ShoppingCart)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-23 13:21:21
 */
@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {

}

