package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import com.edu.cqupt.shemining.service.RegionService;
import com.edu.cqupt.shemining.service.TableDescribeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Api(tags = "6.地区管理")
@RestController
@RequestMapping("/api/region")
@CrossOrigin
public class RegionController {

    @Autowired
    private TableDescribeService tableDescribeService;

    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "检查是否有地区")
    @GetMapping("/checkRegion")
    public Result checkRegion(@RequestParam("tableName") String tableName){
        TableDescribeEntity tableDescribeEntity = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_name", tableName));
        Integer region = tableDescribeEntity.getRegion();
        if (region == 1){
            return Result.success(200,"有地区",tableDescribeEntity);
        }else {
            return Result.fail(201, "没有地区",tableDescribeEntity);
        }
    }


    @ApiOperation(value = "1.检验是否有省份")
    @PostMapping("/checkProvince")
    public Integer checkProvince(@RequestParam String province){
        List<String> province1 = regionService.checkProvince();
        for (int i=0; i<province1.size(); i++){
            if (Objects.equals(province1.get(i), province)){
                return 1;
            }
        }
        return 0;
    }

    @ApiOperation(value = "2.检验是否有市级")
    @PostMapping("/checkCity")
    public Integer checkCity(@RequestParam String city){
        List<String> city1 = regionService.checkCity();
        for (int i=0; i<city1.size(); i++){
            if (Objects.equals(city1.get(i), city)){
                return 1;
            }
        }
        return 0;
    }

    @ApiOperation(value = "3.检验是否有区县")
    @PostMapping("/checkCounty")
    public Integer checkCounty(@RequestParam String county){
        List<String> county1 = regionService.checkCounty();
        for (int i=0; i<county1.size(); i++){
            if (Objects.equals(county1.get(i), county)){
                return 1;
            }
        }
        return 0;
    }



}
