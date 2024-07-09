package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.User;


public interface UserService extends IService<User> {


    User getUserByUserName(String username);

    /**
     * 下面方法是管理员端-数据管理新增
     * @param uid
     * @param tableSize
     */

    void addTableSize(Integer uid, float tableSize);
    void minusTableSize(Integer uid, float tableSize);
}
