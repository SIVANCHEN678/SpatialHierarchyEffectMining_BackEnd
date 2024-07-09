package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.Table;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableMapper extends BaseMapper<Table> {
    List<String> getColumnName(String tableName);
    List<Map<String, Object>> getInfoByTableName(String tableName);

    List<Integer> getDiseaseDict();
}
