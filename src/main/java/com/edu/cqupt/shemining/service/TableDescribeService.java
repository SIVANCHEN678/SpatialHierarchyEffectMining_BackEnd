package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

// TODO 公共模块新增类
public interface TableDescribeService extends IService<TableDescribeEntity> {


    @Transactional
    List<String> uploadDataTable(MultipartFile file, String pid, String tableName, String userName, String classPath, Integer uid, String tableStatus, double tableSize, Integer current_uid,
                                 String dataType,
                                 Integer region,
                                 String diseaseName,
                                 String uidList) throws IOException, ParseException;

    List<TableDescribeEntity> selectAllDataInfo();

    List<TableDescribeEntity> selectDataByName(String searchType, String name);
    TableDescribeEntity selectDataById(String id);

    void deleteByTableName(String tablename);
    void deleteByTableId(String tableId);

    void updateById(String id, String tableName, String tableStatus);
    void updateDataBaseTableName(String old_name, String new_name);

    void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus);
}
