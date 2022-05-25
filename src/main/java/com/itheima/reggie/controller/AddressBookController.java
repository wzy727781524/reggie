package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 地址管理(AddressBook)表控制层
 *
 * @author makejava
 * @since 2022-05-22 15:41:00
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    /**
     * 服务对象
     */
    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook)
    {
        Long currentId = BaseContext.getCurrentId();
        addressBook.setUserId(currentId);
        addressBookService.save(addressBook);
        return R.success("新增成功!");
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> Setdefault(@RequestBody AddressBook addressBook)
    {
        LambdaUpdateWrapper<AddressBook> lw=new LambdaUpdateWrapper<>();
        lw.eq(AddressBook::getUserId,BaseContext.getCurrentId()).set(AddressBook::getIsDefault,0);
        //SQL: update address_book set is_default=0 where User_id=?
        addressBookService.update(lw);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default where id=?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);

    }

    /**
     * 根据id查地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id)
    {
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook!=null)
        {
            return R.success(addressBook);
        }
        return R.error("没有找到对象");
    }

    /**
     * 查询默认的地址
     * @return
     */
    @GetMapping("default")
    public R getDefault()
    {
        LambdaQueryWrapper<AddressBook> lw=new LambdaQueryWrapper<>();
        lw.eq(AddressBook::getUserId,BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lw);
        if(null==addressBook)
        {
            return R.error("没有找到对象");
        }
        return R.success(addressBook);
    }
    /**
     * 查询指定用户全部地址
     * 地址显示
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook)
    {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}",addressBook);
        //条件构造器
        LambdaQueryWrapper<AddressBook> lw=new LambdaQueryWrapper<>();
        lw.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        lw.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(lw));

    }

}

