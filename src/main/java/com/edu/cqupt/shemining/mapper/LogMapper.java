package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.Log;
import com.edu.cqupt.shemining.model.LogEntity;

import java.util.List;

public interface LogMapper extends BaseMapper<LogEntity> {
    List<LogEntity> getAllLogs();
}
