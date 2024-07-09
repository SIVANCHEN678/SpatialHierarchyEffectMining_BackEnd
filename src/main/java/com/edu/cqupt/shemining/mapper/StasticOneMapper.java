package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.StasticOne;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface StasticOneMapper extends BaseMapper<StasticOne> {
    Integer getDieaseCount();

    Integer getSampleCount(String tableName);

    List<String> getTableNames();

    Date getEarlyDate(String tableName);

    Date getLastDate(String tableName);

    Integer getTaskCount();

    List<String> getUserBuildTableNames();

    List<String> getAllUserBuiltTableNames();

    String getType(String tablename);

    Integer getPosNumber(@Param("tablename") String tablename, @Param("type") String type);

    Integer getNegNumber(@Param("tablename") String tablename, @Param("type") String type);
}
