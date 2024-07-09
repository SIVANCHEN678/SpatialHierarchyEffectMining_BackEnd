package com.edu.cqupt.shemining.service.impl;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.AdminDataManageMapper;
import com.edu.cqupt.shemining.mapper.CategoryMapper;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.AdminDataManage;
import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.service.AdminDataManageService;
import com.edu.cqupt.shemining.service.LogService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

/**
* @author hp
* @description 针对表【t_table_manager】的数据库操作Service实现
* @createDate 2023-05-23 15:10:20
*/
@Service
public class AdminDataManageServiceImpl extends ServiceImpl<AdminDataManageMapper, AdminDataManage>
    implements AdminDataManageService {
    @Autowired
    private AdminDataManageMapper adminDataManageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private LogService logService;

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
            adminDataManageMapper.createTable(headers,tableName);
            // 保存表头信息和表数据到数据库中
            for (String[] row : csvData) { // 以此保存每行信息到数据库中
                adminDataManageMapper.insertRow(row,tableName);
            }
        }
        return featureList;
    }
    @Override
    @Transactional
    public List<String> uploadDataTable(MultipartFile file, String pid, String tableName, String userName,
                                        String classPath, Integer uid, String tableStatus, float tableSize,
                                        Integer current_uid) throws IOException, ParseException {
        // 封住表描述信息
        AdminDataManage adminDataManageEntity = new AdminDataManage();
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
        System.out.println("==categoryEntity==" + categoryEntity );
        categoryMapper.insert(categoryEntity);
        logService.insertLog(current_uid, 0, "在category中增加了"+tableName);


        adminDataManageEntity.setTableName(tableName);
        adminDataManageEntity.setTableId(categoryEntity.getId());
        adminDataManageEntity.setCreateUser(userName);
        // 解析系统当前时间
        adminDataManageEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        adminDataManageEntity.setClassPath(classPath);
        adminDataManageEntity.setUid(uid);
        adminDataManageEntity.setTableStatus(tableStatus);
        adminDataManageEntity.setTableSize(tableSize);
        adminDataManageMapper.insert(adminDataManageEntity);
        logService.insertLog(current_uid, 0, "在table_describe中上传了"+tableName);

        userMapper.minusTableSize(uid, tableSize);
        logService.insertLog(current_uid, 0, "在user表中修改容量" );
        List<String> featureList = storeTableData(file, tableName);
        logService.insertLog(current_uid, 0, "在public模式下中创建了"+tableName);
        // 保存数据库
        System.out.println("表描述信息插入成功, 动态建表成功");
        return featureList;
    }

    @Override
    public List<AdminDataManage> selectAllDataInfo() {
        return adminDataManageMapper.selectAllDataInfo();
    }

    @Override
    public List<AdminDataManage> selectDataByName(String searchType, String name) {
        return adminDataManageMapper.selectDataByName(searchType, name);
    }

    @Override
    public AdminDataManage selectDataById(String id) {
        return adminDataManageMapper.selectDataById(id);
    }

    @Override
    public void deleteByTableName(String tableName) {
        adminDataManageMapper.deleteByTableName(tableName);
    }
    @Override
    public void deleteByTableId(String tableId) {
        adminDataManageMapper.deleteByTableId(tableId);
    }

    @Override
    public void updateById(String id, String tableName, String tableStatus) {
        AdminDataManage adminDataManage = adminDataManageMapper.selectById(id);
        String classPath = adminDataManage.getClassPath();
        String[] str = classPath.split("/");
        str[str.length-1] = tableName;
        classPath = String.join("/", str);
        adminDataManage.setClassPath(classPath);
        adminDataManage.setTableName(tableName);
        adminDataManage.setTableStatus(tableStatus);
        adminDataManageMapper.updateById(adminDataManage);
//        adminDataManageMapper.updateById(id, tableName, tableStatus);
    }

    @Override
    public void updateDataBaseTableName(String old_name, String new_name){
        adminDataManageMapper.updateDataBaseTableName(old_name, new_name);
    }

    @Override
    @Transactional
    public void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus, Integer current_uid) {
        updateById(id, tableName, tableStatus);
//        logService.insertLog(current_uid, 0, "更改了table_describe表中的"+oldTableName + "表为：" + tableName + ",将状态更改为：" + tableStatus + "并更改了classpath");
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);
//        logService.insertLog(current_uid, 0, "更改了category表中的"+oldTableName + "表为：" + tableName + ",将状态更改为：" + tableStatus);
        if (!oldTableName.equals(tableName)){
            updateDataBaseTableName(oldTableName, tableName);
//            logService.insertLog(current_uid, 0, "更改了数据库中的"+oldTableName + "表为：" + tableName );
        }
    }


}




