package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 分类管理
 */
@Api(tags = "分类相关接口")

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
      log.info("category:{}",category);
      categoryService.save(category);
      return R.success("新增分类成功");
    }
    /**
     * 分类信息查询
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize){
        //分页构造器
        Page pageInfo=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper<>();
        //添加排序条件,根据sort进行升序排序
        qw.orderByAsc(Category::getSort);
        //进行查询,把数据封装到pageInfo里面的records里面,并返回给页面
        categoryService.page(pageInfo,qw);
        return R.success(pageInfo);
    }
    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类,id为{}",ids);

        categoryService.remove(ids);

        return R.success("分类信息删除成功");
    }
    /**
     * 根据id修改分类
     */
    @PutMapping
    public R<String> updade(@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");

    }
    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")//type
    public R<List<Category>>list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lw=new LambdaQueryWrapper<>();
        //条件添加
        lw.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);


        List<Category> categories = categoryService.list(lw);

        return R.success(categories);
    }


}
