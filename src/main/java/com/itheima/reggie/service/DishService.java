package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {
    /**
     * @param dishDto 新增菜品,同时插入菜品对应的口味数据,需要操作两张表:dish,dish_Flavor
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息 回显
     * @param id
     * @return
     */
    public DishDto geyByidWithFlavor(Long id);

    /**
     * 修改菜品 同时更新二表
     * @param id
     * @return
     */
    public DishDto updateWithFlavor(DishDto dishDto);
}

