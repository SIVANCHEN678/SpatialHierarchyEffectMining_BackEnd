package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.CausalRelationships;
import com.edu.cqupt.shemining.model.Disease;
import com.edu.cqupt.shemining.model.QueryVo.RelationshipsQueryVo;
import com.edu.cqupt.shemining.service.CausalRelationshipsService;
import com.edu.cqupt.shemining.service.DiseaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "TODO舍弃——因果关系")
@RestController
@RequestMapping("/relationship")
public class CausalRelationshipsController {

    @Autowired
    public CausalRelationshipsService causalRelationshipsService;

    @Autowired
    private DiseaseService diseaseService;

    @ApiOperation(value = "查询")
    @GetMapping("/getAllDisease")
    public Result getAllDisease(){
        List<Disease> list = diseaseService.list();
        return Result.success(list);
    }

    @ApiOperation(value = "查询数据")
    @GetMapping("/findTables/{current}/{limit}/{diseaseName}")
    @AutoLog("查询数据")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @PathVariable String diseaseName){
        //创建page对象，传递当前页，每页记录数
        Page<Disease> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Disease> wrapper = new QueryWrapper<>();
        if (!diseaseName.isEmpty()){
            wrapper.like("disease_name",diseaseName);
        }
        //调用方法实现分页查询
        Page<Disease> page1 = diseaseService.page(page, wrapper);

        return Result.success(page1);
    }

    private static final Logger log = LoggerFactory.getLogger(CausalRelationshipsController.class);

    @ApiOperation(value = "查询")
    @GetMapping("/getAll")
    public List<CausalRelationships> getAll(){
        return causalRelationshipsService.list();
    }


    @ApiOperation(value = "删除空间层次效应")
    @GetMapping("/delete/{id}")
    @AutoLog("删除空间层次效应")
    public Result delete(@PathVariable int id){
        boolean b = causalRelationshipsService.removeById(id);
        if (b){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    //3条件查询带分页
    @ApiOperation(value = "条件分页")
    @PostMapping("/findRelationships/{current}/{limit}")
    @AutoLog("查询空间层次效应")
    public Result findRelationships(@PathVariable long current,
                                       @PathVariable long limit,
                                       @RequestBody(required = false) RelationshipsQueryVo relationshipsQueryVo){
        log.info("拦截器已放行，正式调用接口内容，查询管理员信息");
        //创建page对象，传递当前页，每页记录数
        Page<CausalRelationships> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<CausalRelationships> wrapper = new QueryWrapper<>();
        String diseaseName = relationshipsQueryVo.getDiseaseName();
        String algorithmName = relationshipsQueryVo.getAlgorithmName();
        if (!diseaseName.isEmpty()){
            wrapper.like("disease_name",relationshipsQueryVo.getDiseaseName());
        }
        if (!algorithmName.isEmpty()){
            wrapper.like("algorithm_name",relationshipsQueryVo.getAlgorithmName());
        }

        //调用方法实现分页查询
        Page<CausalRelationships> page1 = causalRelationshipsService.page(page, wrapper);

        return Result.success(page1);
    }
}
