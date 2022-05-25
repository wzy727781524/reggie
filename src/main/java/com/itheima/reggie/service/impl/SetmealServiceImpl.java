package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐(Setmeal)表服务实现类
 *
 * @author makejava
 * @since 2022-05-21 01:18:17
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService   {
    /**
     * 新增套餐,同时保存套餐和菜品之间的关系
     * @param setmealDto
     */
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存套餐和菜品的关联的信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((i)->{
            i.setSetmealId(setmealDto.getId());
            return i;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息,执行setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }
    /**
     * 删除套餐,并且删除套餐关联的信息
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWithDish(List<Long> ids) {
        //selecr count(*) from setmeal where id in(1,2)and status=1
        //查询套餐状态,确定是否可以删除
        LambdaQueryWrapper<Setmeal> lw=new LambdaQueryWrapper<>();
        lw.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(lw);
        if(count>0)
        {
            //如果不能删除,抛出一个业务异常
            throw new CustomException("套餐正在售卖中,不能删除!");
        }
        //如果可以删除,先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据---sermeal_dish
        //delete from setmeal_dihs where setmeal_id in(ids)
        LambdaQueryWrapper<SetmealDish> lw2=new LambdaQueryWrapper<>();
        lw2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lw2);

    }
}

