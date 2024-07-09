package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.User;
import com.edu.cqupt.shemining.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUserName(String username) {
        User user = userMapper.queryByUername(username);
        return user;
    }

    /**
     * 下面方法是管理员端-数据管理新增
     * @param uid
     * @param tableSize
     */

    @Override
    public void addTableSize(Integer uid,float tableSize) {
        userMapper.addTableSize(uid, tableSize);
    }

    @Override
    public void minusTableSize(Integer uid, float tableSize) {

        userMapper.minusTableSize(uid, tableSize);
    }

}
