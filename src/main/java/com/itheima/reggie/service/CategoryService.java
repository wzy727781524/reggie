package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
