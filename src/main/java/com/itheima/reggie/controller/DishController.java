package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品口味关系表(DishFlavor)表控制层
 *
 * @author makejava
 * @since 2022-05-21 13:48:05
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    //分页查询要用
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品添加成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, lambdaQueryWrapper);
        //对象拷贝 并且忽略属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //获取List集合 然后遍历
        List<Dish> records = pageInfo.getRecords();
        //获取dishDtoPage里面的list集合
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //遍历records，根据菜品中的id获取分类id
            Long categoryId = item.getCategoryId();
            //根据id进行查询,获得分类对象并且获得分类名字(name)
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                //进行赋值
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息 回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.geyByidWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存数据,来保持与数据库的数据一致
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        //清理一部分缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品添加成功");
    }


    /**
     * 根据菜品分类id查询
     *
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish)
//    {
//        //构造查询对象
//        LambdaQueryWrapper<Dish> lw=new LambdaQueryWrapper<>();
//        //构造查询条件 查询状态是1的(启用)和分类id的菜品
//        lw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
//        //添加排序条件
//        lw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(lw);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> distDtoList = null;
        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        //先从Redis中获取缓存数据
        distDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (distDtoList != null) {
            //如果存在,直接返回,无需查询数据库
            return R.success(distDtoList);
        }


        //如果不存在,需要查询数据库,缓存到Redis中


        //构造查询对象
        LambdaQueryWrapper<Dish> lw = new LambdaQueryWrapper<>();
        //构造查询条件 查询状态是1的(启用)和分类id的菜品
        lw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId()).eq(Dish::getStatus, 1);
        //添加排序条件
        lw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lw);

        distDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //当前菜品的id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lw2 = new LambdaQueryWrapper<>();
            lw2.eq(DishFlavor::getDishId, id);

            List<DishFlavor> dishFlavors = dishFlavorService.list(lw2);
            dishDto.setFlavors(dishFlavors);

            return dishDto;

        }).collect(Collectors.toList());
        //如果不存在,需要查询数据库,缓存到Redis中
        redisTemplate.opsForValue().set(key,distDtoList,5, TimeUnit.MINUTES);
        return R.success(distDtoList);
    }

    /**
     * 菜品停售
     */
    @PostMapping("status/0")
    public R<String> status0(Long[] ids) {
        LambdaUpdateWrapper<Dish> lw = new LambdaUpdateWrapper<>();
        lw.set(Dish::getStatus, 0).in(Dish::getId, ids);
        dishService.update(lw);
        return R.success("停售成功!");
    }

    /**
     * 菜品启售
     */
    @PostMapping("status/1")
    public R<String> status1(Long[] ids) {
        LambdaUpdateWrapper<Dish> lw = new LambdaUpdateWrapper<>();

        lw.set(Dish::getStatus, 1).in(Dish::getId, ids);
        dishService.update(lw);
        return R.success("启售成功!");
    }

    /**
     * 菜品删除
     */
    @DeleteMapping
    public R<String> deleteByid(Long[] ids) {
        dishService.removeByIds(Arrays.asList(ids));
        return R.success("删除成功!");
    }
}

