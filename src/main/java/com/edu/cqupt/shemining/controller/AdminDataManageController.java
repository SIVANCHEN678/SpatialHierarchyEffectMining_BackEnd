package com.edu.cqupt.shemining.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.mapper.AdminDataManageMapper;
import com.edu.cqupt.shemining.mapper.CategoryMapper;
import com.edu.cqupt.shemining.mapper.UserMapper;
import com.edu.cqupt.shemining.model.AdminDataManage;
import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import com.edu.cqupt.shemining.model.User;
import com.edu.cqupt.shemining.service.*;
import com.edu.cqupt.shemining.vo.CheckApprovingVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

// TODO 因为操作该模块的用户肯定是管理员，因此在插入日志时将role角色固定为0， 管理员状态
@Api(tags = "1.admin——管理员")
@RestController
@RequestMapping("/admin/sysManage")
public class AdminDataManageController {
    @Autowired
    AdminDataManageService adminDataManageService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    private LogService logService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminDataManageMapper adminDataManageMapper;

    @GetMapping("/selectUsernamesById")
    public Result<AdminDataManage> selectUsernamesById(
            @RequestParam("id") String id
    ){
        AdminDataManage adminDataManage = adminDataManageService.selectDataById(id); // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        if (checkApproving == null || checkApproving.length() == 0){
            return Result.fail(501, "没有申请人",null);
        }
        String[] approvingUsernames = checkApproving.split(",");

        List<CheckApprovingVo> checkApprovingVos = new ArrayList<>();


        for( String userName: approvingUsernames){
            // 添加查询条件，假设我们要搜索 userName 为 "testUser" 的用户
            // 使用 QueryWrapper 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", userName);
            User user = userMapper.selectOne(queryWrapper);

            CheckApprovingVo checkApprovingVo = new CheckApprovingVo();
            checkApprovingVo.setUsername(user.getUserName());
            checkApprovingVo.setUid(user.getUid());
            checkApprovingVo.setClasspath(adminDataManage.getClassPath());
            checkApprovingVos.add(checkApprovingVo);
        }

        System.out.println(checkApprovingVos);

        return Result.success("200",checkApprovingVos);
    }

    @GetMapping("/updateCheckApprove")
    public Result<AdminDataManage> updateCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("userName") String userName,
            @RequestParam("type") int type
    ){
        AdminDataManage adminDataManage = adminDataManageService.selectDataById(id);
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        String checkApproved = adminDataManage.getCheckApproved();
        String[] approvingUsernames = checkApproving.split(",");
        List<String> itemList = new ArrayList<>(Arrays.asList(approvingUsernames));

        itemList.remove(userName);  // 不管同意还是拒绝下载，都要把他从申请列表中删除
        if (type == 1){   // 如果同意下载，还需要将他添加到已同意列表中去
            if (checkApproved == null || checkApproved.length() == 0){
                checkApproved = userName;
            }else{
                checkApproved += ","+userName;
            }

        }

        adminDataManage.setCheckApproving(String.join(",", itemList));
        adminDataManage.setCheckApproved(checkApproved);

        adminDataManageMapper.updateById(adminDataManage);
        return Result.success("200","成功更改");
    }

    @GetMapping("/getCheckApprove")
    public Result<AdminDataManage> getCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("userName") String userName
    ){
        QueryWrapper<AdminDataManage> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<AdminDataManage> list = adminDataManageService.list(wrapper);

        AdminDataManage adminDataManage = adminDataManageService.selectDataById(list.get(0).getId());
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        String checkApproved = adminDataManage.getCheckApproved();
        List<String> approvingItemList = new ArrayList<>(Arrays.asList());
        List<String> approvedItemList = new ArrayList<>(Arrays.asList());
        if (checkApproving != null) {
            String[] approvingUsernames = checkApproving.split(",");
            approvingItemList = new ArrayList<>(Arrays.asList(approvingUsernames));
        }
        if (checkApproved != null){
            String[] approvedUsernames = checkApproved.split(",");
            approvedItemList = new ArrayList<>(Arrays.asList(approvedUsernames));
        }
        if (approvedItemList.contains(userName)){
            return Result.success("200","2");
        }
        else if (approvingItemList.contains(userName)){
            return Result.success("200","1");
        }else{
            return Result.success("500","0");
        }

    }

    @GetMapping("/applyCheckApprove")
    public Result<AdminDataManage> applyCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("userName") String userName
    ){
        QueryWrapper<AdminDataManage> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<AdminDataManage> list = adminDataManageService.list(wrapper);

        AdminDataManage adminDataManage = adminDataManageService.selectDataById(list.get(0).getId());
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        if (checkApproving == null || checkApproving.length() == 0){
            checkApproving = userName;
        }else{
            checkApproving += ","+userName;
        }
        adminDataManage.setCheckApproving(checkApproving);
        adminDataManageMapper.updateById(adminDataManage);
        return Result.success(userName+"成功申请下载数据集"+adminDataManage.getTableName(),"1");
    }

    @GetMapping("/allowCheckApprove")
    public Result<AdminDataManage> allowCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("userName") String userName
    ){
        QueryWrapper<AdminDataManage> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<AdminDataManage> list = adminDataManageService.list(wrapper);

        AdminDataManage adminDataManage = adminDataManageService.selectDataById(list.get(0).getId());  // 根据id获取table_describe表的那一行
        // 根据id获取table_describe表的那一行
        String checkApproved = adminDataManage.getCheckApproved();
        String[] approvedUsernames = checkApproved.split(",");
        List<String> approvedItemList = new ArrayList<>(Arrays.asList(approvedUsernames));
        approvedItemList.remove(userName);
        adminDataManage.setCheckApproved(String.join(",", approvedItemList));
        adminDataManageMapper.updateById(adminDataManage);
        return Result.success(userName+"成功下载数据集"+adminDataManage.getTableName()+"并取消下次下载权限","0");
//        return Result.success("200",);
    }



    // 文件上传
    @PostMapping("/uploadDataTable")
    public Result uploadDataTable(@RequestParam("file") MultipartFile file,
//                             @RequestParam("tableId") String tableId,
                                  @RequestParam("pid") String pid,
                                  @RequestParam("tableName") String tableName,
                                  @RequestParam("userName") String userName,
//                                  @RequestParam("classPath") String classPath,
                                  @RequestParam("uid") Integer uid,   // 传表中涉及到的用户的uid
                                  @RequestParam("tableStatus") String tableStatus,
                                  @RequestParam("tableSize") float tableSize,
                                  @RequestParam("current_uid") Integer current_uid, //操作用户的uid
                                  @RequestParam("ids") String[] ids

        ){
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
            List<String> featureList = adminDataManageService.uploadDataTable(file, pid, tableName, userName, classPath, uid, tableStatus, tableSize, current_uid);
            return Result.success(200,"数据上传成功"); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
            logService.insertLog(current_uid, 0, e.getMessage());
            return Result.success(500,"文件上传异常");
        }
//        // 保存表数据信息
//        try {
////            String tableId="";
//            List<String> featureList = adminDataManageService.uploadDataTable(file, pid, tableName, userName, classPath, uid, tableStatus, tableSize, current_uid);
//            return Result.success("200",featureList); // 返回表头信息
//        }catch (Exception e){
//            e.printStackTrace();
//            logService.insertLog(current_uid, 0, e.getMessage());
//            return Result.success(500,"文件上传异常");
//        }
    }



    @GetMapping("/selectAdminDataManage")
    public Result<AdminDataManage> selectAdminDataManage(
//            @RequestParam("current_uid") String current_uid
    ){ // 参数表的Id
//        List<AdminDataManage> adminDataManages = adminDataManageService.selectAllDataInfo();
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        List<AdminDataManage> adminDataManages = adminDataManageService.list();
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success("200",ret);
//        return Result.success("200",adminDataManages);
    }

    @GetMapping("/selectDataByName")
    public Result<AdminDataManage> selectDataByName(
            @RequestParam("searchType") String searchType,
            @RequestParam("name") String name
//            @RequestParam("current_uid") String current_uid
    ){
        List<AdminDataManage> adminDataManages = adminDataManageService.selectDataByName(searchType, name);
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success(200,"",ret);
    }

    @GetMapping("/selectDataById")
    public Result<AdminDataManage> selectDataById(
            @RequestParam("tableId") String tableId
//            @RequestParam("current_uid") String current_uid
    ){
//        AdminDataManage adminDataManage = adminDataManageService.selectDataById(id);
        AdminDataManage adminDataManage = adminDataManageService.getOne(new QueryWrapper<AdminDataManage>().eq("table_id", tableId));
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        return Result.success(200,"",adminDataManage);
    }

    @GetMapping("/updateAdminDataManage")
    public Result<AdminDataManage> updateAdminDataManage(
            @RequestParam("id") String id,
            @RequestParam("tableid") String tableid,
            @RequestParam("oldTableName") String oldTableName,
            @RequestParam("tableName") String tableName,
            @RequestParam("tableStatus") String tableStatus,
            @RequestParam("current_uid") Integer current_uid
            ){
        adminDataManageService.updateInfo(id, tableid, oldTableName, tableName, tableStatus, current_uid);

        return Result.success("200","已经更改到数据库");
    }

    @GetMapping("/getLevel2Label")
    public Result<List<CategoryEntity>> getLevel2Label(
//            @RequestParam("current_uid") String current_uid
    ){
        List<CategoryEntity> res = categoryService.getLevel2Label();
        return Result.success("200",res);
    }
    @GetMapping("/getLabelByPid")
    public Result<List<CategoryEntity>> getLabelsByPid(
            @RequestParam("pid") String pid
//            @RequestParam("current_uid") String current_uid
    ){
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        return Result.success("200",res);
    }

    @GetMapping("/deleteByTableName")
    public Result<AdminDataManage> deleteByTableName(
//            @RequestParam("id") Integer id,
            @RequestParam("uid") Integer uid,
            @RequestParam("tableSize") float tableSize,
            @RequestParam("tableId") String tableId,
            @RequestParam("tableName") String tableName,
            @RequestParam("current_uid") Integer current_uid
    ){
//        System.out.println();

        adminDataManageService.deleteByTableName(tableName);// 【因为数据库中表名是不能重名的】
//        logService.insertLog(current_uid, 0, "删除了public模式下储存的表:" + tableName);
        adminDataManageService.deleteByTableId(tableId);// 在table_describe中删除记录
//        logService.insertLog(current_uid, 0, "删除了table_describe表中的"+tableName+"记录信息");
        categoryService.removeNode(tableId);// 在category中设置is_delete为1
//        logService.insertLog(current_uid, 0, "在category中设置is_delete为1" );

//        float tableSize = adminDataManage.getTableSize();
        userService.addTableSize(uid, tableSize);
//        logService.insertLog(current_uid, 0, "在user表中修改容量" );
        return Result.success(200,"已在数据库中删除了"+tableName+"表");
    }

    // 管理员端-数据管理新更改
    @GetMapping("/selectDataDiseases")
    public Result<AdminDataManage> selectDataDiseases(
//            @RequestParam("current_uid") String current_uid
    ){ // 参数表的Id
        List<CategoryEntity> res = categoryService.getLevel2Label();

        List<Object> retList = new ArrayList<>();
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }

            retList.add(ret);
        }
        System.out.println(retList);


        return Result.success("200",retList);
    }

    // 管理员端-数据管理新更改
    public List<Map<String, Object>> selectCategoryDataDiseases(String pid){
        List<Map<String, Object>> retList = new ArrayList<>();
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }
            retList.add(ret);
        }
        return retList;
    }
}
