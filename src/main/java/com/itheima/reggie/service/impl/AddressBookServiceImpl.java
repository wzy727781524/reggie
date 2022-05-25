package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookDao;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * 地址管理(AddressBook)表服务实现类
 *
 * @author makejava
 * @since 2022-05-22 15:41:01
 */
@Service("addressBookService")
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {

}

