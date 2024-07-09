package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.UserLogMapper;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.UserLogService;
import org.springframework.stereotype.Service;

@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog> implements UserLogService {
}
