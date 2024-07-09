package com.edu.cqupt.shemining.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.mapper.CategoryMapper;
import com.edu.cqupt.shemining.mapper.TableDescribeMapper;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import com.edu.cqupt.shemining.service.CategoryService;
import com.edu.cqupt.shemining.service.FeatureService;
import com.edu.cqupt.shemining.service.TableDescribeService;
import com.edu.cqupt.shemining.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类
@Api(tags = "（6）公共数据表述")
@RestController
@RequestMapping("/api")
public class TableDescribeController {
    @Autowired
    TableDescribeService tableDescribeService;
    @Autowired
    TableDescribeMapper tableDescribeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    FeatureService featureService;

    @GetMapping("/tableDescribe")
    public Result<TableDescribeEntity> getTableDescribe(@RequestParam("id") String id){ // 参数表的Id
        TableDescribeEntity tableDescribeEntity = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_id", id));
        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        return Result.success("200",JSON.toJSONString(tableDescribeEntity));
    }

    @GetMapping("/getTableNumber")
    public Result getTableNumber(){ // 参数表的Id
        QueryWrapper<TableDescribeEntity> queryWrapper = new QueryWrapper<>();

        int count = Math.toIntExact(tableDescribeMapper.selectCount(queryWrapper));

        return Result.success("200",JSON.toJSONString(count));
    }

    // 文件上传
    @PostMapping("/uploadDataTable")
    public Result uploadDataTable(@RequestParam("file") MultipartFile file,
//                             @RequestParam("tableId") String tableId,
                                  @RequestParam("pid") String pid,
                                  @RequestParam("tableName") String tableName,
                                  @RequestParam("userName") String userName,
                                  @RequestParam("ids") String[] ids,
//                                  @RequestParam("classPath") String classPath,
                                  @RequestParam("uid") Integer uid,   // 传表中涉及到的用户的uid
                                  @RequestParam("tableStatus") String tableStatus,
                                  @RequestParam("tableSize") double tableSize,
                                  @RequestParam("current_uid") Integer current_uid, //操作用户的uid

                                  @RequestParam("dataType")     String dataType,    //操作用户的uid
                                  @RequestParam("region")       Integer region,      //操作用户的uid
                                  @RequestParam("diseaseName")  String diseaseName,  //操作用户的uid
                                  @RequestParam("uid_list")     String uidList  //操作用户的uid

    ){
        // 保存表数据信息
        try {
//            String tableId="";
            // 管理员端-数据管理新更改
//            传入的是category的id集合，根据id获取labels拼接成classpath
            String classPath = "公共数据集";
            for (String id : ids){
                CategoryEntity categoryEntity = categoryMapper.selectById(id);
                classPath += "/" + categoryEntity.getLabel();
            }
            classPath += "/" + tableName;
            List<String> featureList = tableDescribeService.uploadDataTable(file, pid, tableName, userName, classPath, uid, tableStatus, tableSize, current_uid,
                    dataType,region,diseaseName,uidList);
            return Result.success(200,"文件上传成功",featureList); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
//            logService.insertLog(Integer.parseInt(current_uid), 0, e.getMessage());
            return Result.success(500,"文件上传异常");
        }
    }

    @GetMapping("/selectAdminDataManage")
    public Result<TableDescribeEntity> selectAdminDataManage(){ // 参数表的Id
        List<TableDescribeEntity> adminDataManages = tableDescribeService.selectAllDataInfo();
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success("200",ret);
//        return Result.success("200",adminDataManages);
    }


    @GetMapping("/selectDataByName")
    public Result<TableDescribeEntity> selectDataByName(
            @RequestParam("searchType") String searchType,
            @RequestParam("name") String name){
        List<TableDescribeEntity> adminDataManages = tableDescribeService.selectDataByName(searchType, name);
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success("200",ret);
    }

    @GetMapping("/selectDataById")
    public Result<TableDescribeEntity> selectDataById(
            @RequestParam("id") String id){
        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(id);
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        return Result.success("200",adminDataManage);
    }


    @GetMapping("/updateAdminDataManage")
    public Result<TableDescribeEntity> updateAdminDataManage(
            @RequestParam("id") String id,
            @RequestParam("tableid") String tableid,
            @RequestParam("oldTableName") String oldTableName,
            @RequestParam("tableName") String tableName,
            @RequestParam("tableStatus") String tableStatus
    ){
        tableDescribeService.updateInfo(id, tableid, oldTableName, tableName, tableStatus);

        return Result.success("200","已经更改到数据库");
    }



    @GetMapping("/getLevel2Label")
    public Result<List<CategoryEntity>> getLevel2Label(
    ){
        List<CategoryEntity> res = categoryService.getLevel2Label();
        return Result.success("200",res);
    }
    @GetMapping("/getLabelByPid")
    public Result<List<CategoryEntity>> getLabelsByPid(
            @RequestParam("pid") String pid
    ){
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        return Result.success("200",res);
    }


    @GetMapping("/deleteByTableName")
    public Result<TableDescribeEntity> deleteByTableName(
            @RequestParam("id") String id,
            @RequestParam("uid") Integer uid,
            @RequestParam("tableSize") Double tableSize,
            @RequestParam("tableId") String tableId,
            @RequestParam("tableName") String tableName
    ){
//        System.out.println();
        tableDescribeService.deleteByTableName(tableName);// 【因为数据库中表名是不能重名的】
        tableDescribeService.deleteByTableId(tableId);// 在table_describe中删除记录
        categoryService.removeNode(tableId);// 在category中设置is_delete为1

//        float tableSize = adminDataManage.getTableSize();
        userMapper.recoveryUpdateUserColumnById(uid, tableSize);
        return Result.success("200","已在数据库中删除了"+tableName+"表");
    }
}
