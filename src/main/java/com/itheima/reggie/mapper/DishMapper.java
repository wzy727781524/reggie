package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品管理(Dish)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-21 01:14:21
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}

