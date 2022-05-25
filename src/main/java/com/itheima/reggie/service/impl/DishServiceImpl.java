package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理(Dish)表服务实现类
 *
 * @author makejava
 * @since 2022-05-21 01:12:57
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * @param dishDto 新增菜品,同时插入菜品对应的口味数据,需要操作两张表:dish,dish_Flavor
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品
        this.save(dishDto);
        //获取菜品的id
        Long id = dishDto.getId();

//        遍历对菜品id进行赋值
//        for (DishFlavor flavor : dishDto.getFlavors()) {
//            flavor.setDishId(id);
//        }

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存口味
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    /**
     * 根据id查询菜品信息和口味信息 回显
     * @param id
     * @return
     */
    @Override
    public DishDto geyByidWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> lw=new LambdaQueryWrapper<>();
        lw.eq(DishFlavor::getDishId,dish.getId());
        //进行查询
        List<DishFlavor> flavors = dishFlavorService.list(lw);
        //set到DishDto里面
        dishDto.setFlavors(flavors);

        return dishDto;
    }
    /**
     * 修改菜品 同时更新二表
     * @param
     * @return
     */
    @Override
    public DishDto updateWithFlavor(DishDto dishDto) {
        //更新dish的基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味的数据--dish_flavor表的delete
        LambdaQueryWrapper<DishFlavor> lw=new LambdaQueryWrapper<>();
        lw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lw);
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置dishID
        flavors= flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        return dishDto;
    }
}

