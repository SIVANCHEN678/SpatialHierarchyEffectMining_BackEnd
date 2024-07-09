package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.LogEntity;

import java.util.List;

public interface LogService extends IService<LogEntity> {
    List<LogEntity> getAllLogs();
    void insertLog(Integer uid, Integer role, String operation);
}
