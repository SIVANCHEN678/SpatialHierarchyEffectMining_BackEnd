package com.edu.cqupt.shemining.controller;

import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.FieldManagementEntity;
import com.edu.cqupt.shemining.service.*;
import com.edu.cqupt.shemining.vo.QueryFiledVO;
import com.edu.cqupt.shemining.vo.UpdateFiledVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "4.公共特征管理")
@RestController
@RequestMapping("/api/filed")
public class FiledController {

    @Autowired
    TableDataService tableDataService;
    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    TableDescribeService tableDescribeService;

    @Autowired
    private FieldManagementService fieldManagementService;


    /**
     *
     * 通过关联疾病名称展示字段信息
     * @param
     * @return 字段信息表
     */
    @PostMapping("/getAllFiled")
    public Result getAllFiled(@RequestBody QueryFiledVO queryFiledVO){
        System.out.println(queryFiledVO.getDiseaseName());
        List<FieldManagementEntity> res = fieldManagementService.getFiledByDiseaseName(queryFiledVO.getDiseaseName());
        return Result.success(res);
    }


    /**
     *
     * 新建特征表 根据动态选择来更新字段表
     *
     * 接收病种名字 和 字段列表
     */
    @PostMapping("/updateFiled")
    public Result updateFiled(@RequestBody UpdateFiledVO updateFiledVO){
        String diseaseName = updateFiledVO.getDiseaseName();
        List<String> fields = updateFiledVO.getFileds();
        // 更新字段表信息
        fieldManagementService.updateFieldsByDiseaseName(diseaseName, fields);
        return Result.success(200,"成功");
    }





}
