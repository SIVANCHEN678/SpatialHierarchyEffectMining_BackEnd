package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.Table;

import java.util.List;
import java.util.Map;

public interface TableService extends IService<Table> {
    public List<String> getColumnName(String tableName);
    List<Map<String, Object>> getInfoByTableName(String diseaseName);
}
