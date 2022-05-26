package com.itheima.reggie.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车(ShoppingCart)表控制层
 *
 * @author makejava
 * @since 2022-05-23 13:21:21
 */
@Slf4j
@Api(tags = "购物车相关接口")

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    /**
     * 服务对象
     */
    @Resource
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id,指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //判断添加到购物车的是菜品还是套餐
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lw=new LambdaQueryWrapper<>();
        lw.eq(ShoppingCart::getUserId,currentId);

        if(dishId!=null)
        {
            //添加到购物车的是菜品
            lw.eq(ShoppingCart::getDishId,dishId);
        }
        else
        {
            //添加到购物车的是套餐
            lw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或者是套餐是否在购物车中
        //SQL:select * from shopping_cart where User_id=? where Dish_id=?(Sermea_id=?)
        ShoppingCart cart = shoppingCartService.getOne(lw);

        if(cart!=null)
        {
            //已经存在,直接在原来的基础上加一
            Integer number = cart.getNumber();
            cart.setNumber(number+1);
            shoppingCartService.updateById(cart);
        }
        else {
            //不存在 传过来的菜品或者套餐设置默认数量为一然后存入购物车
            //
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart=shoppingCart;
        }
        return R.success(cart);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list()
    {
        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        //根据当前用户的id查询购物车的数据并把数据返回给页面
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentId);
        //按照加入购物车的时间进行升序排序
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        //进行查询
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);
        //返回
        return R.success(shoppingCarts);
    }
    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean()
    {
        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        //构造条件
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        //根据用户名删除的条件
        lqw.eq(ShoppingCart::getUserId,currentId);
        //进行删除操作
        shoppingCartService.remove(lqw);
        //返回
        return R.success("清空成功!");
    }
}

