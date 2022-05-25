package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单表(Orders)表控制层
 *
 * @author makejava
 * @since 2022-05-23 14:56:05
 */
@Slf4j
@RestController
@RequestMapping("order")
public class OrdersController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private OrdersService ordersService;
    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders)
    {
        log.info("订单数据:{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }
    /**
     * 分页查询订单
     */
    @GetMapping("/userPage")
    public R<Page> list(int page, int pageSize){
        Long currentId = BaseContext.getCurrentId();
        Page<Orders>  pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId,currentId);
        ordersService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }
    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public R<Page> backlist(int page, int pageSize){

        Page<Orders>pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }
}

