package com.itheima.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品口味关系表(DishFlavor)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-21 13:48:05
 */
@Mapper
public interface DishFlavorDao extends BaseMapper<DishFlavor> {


}

