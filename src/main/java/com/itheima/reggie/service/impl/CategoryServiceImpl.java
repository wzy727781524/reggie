package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类,删除之前需要金星判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品,如果关联,就抛出异常
        //添加查询条件,根据分类id进行查询
        LambdaQueryWrapper<Dish> lw = new LambdaQueryWrapper<>();
        lw.eq(Dish::getCategoryId, id);
        int count = dishService.count(lw);
        if (count > 0) {
            //已关联，抛出业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        //查询当前分类是否关联了套餐,如果关联,就抛出异常
        //添加查询条件,根据分类id进行查询
        LambdaQueryWrapper<Setmeal> lw2 = new LambdaQueryWrapper<>();
        lw2.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(lw2);
        if (count2 > 0) {
            //已关联，抛出业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
