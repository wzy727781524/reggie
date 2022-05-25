package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;



/**
 * 地址管理(AddressBook)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-22 15:41:01
 */
@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {

}

