package com.edu.cqupt.shemining.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.CategoryMapper;
import com.edu.cqupt.shemining.mapper.TableDescribeMapper;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.TableDescribeService;
import com.edu.cqupt.shemining.service.UserLogService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// TODO 公共模块新增类

@Service
public class TableDescribeServiceImpl extends ServiceImpl<TableDescribeMapper, TableDescribeEntity> implements TableDescribeService {

    @Autowired
    private TableDescribeMapper tableDescribeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserLogService userLogService;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
        ArrayList<String> featureList = null;
        if (!file.isEmpty()) {
            // 使用 OpenCSV 解析 CSV 文件
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            // 获取表头信息
            String[] headers = csvData.get(0);
            featureList = new ArrayList<String>(Arrays.asList(headers));
            System.out.println("表头信息为："+ JSON.toJSONString(headers));
            // 删除表头行，剩余的即为数据行
            csvData.remove(0);
            // 创建表信息
            tableDescribeMapper.createTable(headers,tableName);
            // 保存表头信息和表数据到数据库中
            for (String[] row : csvData) { // 以此保存每行信息到数据库中
                tableDescribeMapper.insertRow(row,tableName);
            }
        }
        return featureList;
    }



    @Override
    @Transactional
    public List<String> uploadDataTable(MultipartFile file, String pid, String tableName, String userName,
                                        String classPath, Integer uid, String tableStatus, double tableSize,
                                        Integer current_uid,
                                        String dataType,
                                        Integer region,
                                        String diseaseName,
                                        String uidList) throws IOException, ParseException {
        // 封住表描述信息
        TableDescribeEntity tableDescribeEntity = new TableDescribeEntity();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCatLevel(4);
        categoryEntity.setLabel(tableName);
        categoryEntity.setParentId(pid);
        categoryEntity.setIsLeafs(1);
        categoryEntity.setIsDelete(0);
        categoryEntity.setUid(uid);
        categoryEntity.setStatus(tableStatus);
        categoryEntity.setUserName(userName);
        categoryEntity.setIsUpload("1");
        categoryEntity.setIsFilter("0");
        categoryEntity.setUidList(uidList);

        System.out.println("==categoryEntity==" + categoryEntity );
        categoryMapper.insert(categoryEntity);
        UserLog userLog = new UserLog();
        userLog.setUserName(userName);
        userLog.setUid(current_uid);
        userLog.setOpType("在category中增加了"+tableName);
        userLogService.save(userLog);
//        userLogService.insertLog(current_uid, 0, "在category中增加了"+tableName);


        tableDescribeEntity.setTableName(tableName);
        tableDescribeEntity.setTableId(categoryEntity.getId());
        tableDescribeEntity.setCreateUser(userName);
        // 解析系统当前时间
        tableDescribeEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tableDescribeEntity.setClassPath(classPath);
        tableDescribeEntity.setUid(uid);
        tableDescribeEntity.setTableStatus(tableStatus);
        tableDescribeEntity.setTableSize(tableSize);
        tableDescribeEntity.setDataType(dataType);
        tableDescribeEntity.setRegion(region);
        tableDescribeEntity.setDiseaseName(diseaseName);

        tableDescribeMapper.insert(tableDescribeEntity);

        userLog.setOpType("在table_describe中上传了"+tableName);
        userLogService.save(userLog);
//        userLogService.insertLog(current_uid, 0, "在table_describe中上传了"+tableName);

        userMapper.decUpdateUserColumnById(uid, tableSize);
        userLog.setOpType("在user表中修改容量");
        userLogService.save(userLog);
//        userLogService.insertLog(current_uid, 0, "在user表中修改容量" );
        List<String> featureList = storeTableData(file, tableName);
        userLog.setOpType("在public模式下中创建了"+tableName);
        userLogService.save(userLog);
//        userLogService.insertLog(current_uid, 0, "在public模式下中创建了"+tableName);
        // 保存数据库
        System.out.println("表描述信息插入成功, 动态建表成功");
        return featureList;
    }

    @Override
    public List<TableDescribeEntity> selectAllDataInfo() {
        return tableDescribeMapper.selectAllDataInfo();
    }

    @Override
    public List<TableDescribeEntity> selectDataByName(String searchType, String name) {
        return tableDescribeMapper.selectDataByName(searchType, name);
    }

    @Override
    public TableDescribeEntity selectDataById(String id) {
        return tableDescribeMapper.selectDataById(id);
    }

    @Override
    public void deleteByTableName(String tableName) {
        tableDescribeMapper.deleteByTableName(tableName);
    }
    @Override
    public void deleteByTableId(String tableId) {
        tableDescribeMapper.deleteByTableId(tableId);
    }

    @Override
    public void updateById(String id, String tableName, String tableStatus) {
        TableDescribeEntity adminDataManage = tableDescribeMapper.selectById(id);
        String classPath = adminDataManage.getClassPath();
        String[] str = classPath.split("/");
        str[str.length-1] = tableName;
        classPath = String.join("/", str);
        adminDataManage.setClassPath(classPath);
        adminDataManage.setTableName(tableName);
        adminDataManage.setTableStatus(tableStatus);
        tableDescribeMapper.updateById(adminDataManage);
//        adminDataManageMapper.updateById(id, tableName, tableStatus);
    }

    @Override
    public void updateDataBaseTableName(String old_name, String new_name){
        tableDescribeMapper.updateDataBaseTableName(old_name, new_name);
    }

    @Override
    @Transactional
    public void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus) {
        updateById(id, tableName, tableStatus);
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);
        updateDataBaseTableName(oldTableName, tableName);
    }


}
