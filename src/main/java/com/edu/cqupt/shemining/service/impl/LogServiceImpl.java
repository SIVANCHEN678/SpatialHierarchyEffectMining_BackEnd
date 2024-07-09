package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.LogMapper;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.LogEntity;
import com.edu.cqupt.shemining.model.User;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.LogService;
import com.edu.cqupt.shemining.service.UserLogService;
import com.edu.cqupt.shemining.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements LogService {

    @Autowired
    LogMapper logMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<LogEntity> getAllLogs() {
        return logMapper.getAllLogs();
    }

    public void insertLog(Integer uid, Integer role, String operation) {
//        User user = userMapper.selectByUid(uid);
        User user = userService.getOne(new QueryWrapper<User>().eq("uid", uid));

        LogEntity logEntity = new LogEntity();
        logEntity.setUid(uid);
        logEntity.setUserName(user.getUserName());
        logEntity.setRole(role);
        logEntity.setOperation(operation);
        // 创建 DateTimeFormatter 对象，定义日期时间的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 创建 LocalDateTime 对象，存储当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 使用 formatter 格式化 LocalDateTime 对象
        String formattedDate = now.format(formatter);
        logEntity.setOpTime(formattedDate);

        logMapper.insert(logEntity);
    }
}
