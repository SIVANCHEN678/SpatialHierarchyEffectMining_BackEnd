package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.FieldManagementMapper;
import com.edu.cqupt.shemining.mapper.TableMapper;
import com.edu.cqupt.shemining.model.FieldManagementEntity;
import com.edu.cqupt.shemining.service.FieldManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO 公共模块新增类

@Service
public class FieldManagementServiceImpl extends ServiceImpl<FieldManagementMapper, FieldManagementEntity> implements FieldManagementService {

    @Autowired
    private FieldManagementMapper fieldManagementMapper;

    @Override
    public List<FieldManagementEntity> getFiledByDiseaseName(String diseaseName) {

        List<FieldManagementEntity> res = fieldManagementMapper.getFiledByDiseaseName(diseaseName);

        return res;
    }

    @Override
    public void updateFieldsByDiseaseName(String diseaseName, List<String> fields) {

        fieldManagementMapper.updateFieldsByDiseaseName(diseaseName, fields);
    }

    @Override
    public List<String> getColumnName(String tableName) {
        List<String> tableNames = fieldManagementMapper.getColumnName(tableName);
        return tableNames;
    }
}
