package com.itheima.reggie.controller;


import com.itheima.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单明细表(OrderDetail)表控制层
 *
 * @author makejava
 * @since 2022-05-23 14:56:05
 */
@Slf4j
@RestController
@RequestMapping("orderDetail")
public class OrderDetailController  {
    /**
     * 服务对象
     */
    @Resource
    private OrderDetailService orderDetailService;



}

