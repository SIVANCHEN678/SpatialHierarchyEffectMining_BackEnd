package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.TableMapper;
import com.edu.cqupt.shemining.model.Table;
import com.edu.cqupt.shemining.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements TableService {


    @Autowired
    private TableMapper tableMapper;

    @Override
    public List<String> getColumnName(String tableName) {
        List<String> tableNames = tableMapper.getColumnName(tableName);
        return tableNames;
    }
    @Override
    public List<Map<String, Object>> getInfoByTableName(String tableName) {
        return tableMapper.getInfoByTableName(tableName);
    }
}
