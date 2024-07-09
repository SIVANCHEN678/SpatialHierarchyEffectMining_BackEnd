package com.edu.cqupt.shemining.controller;

import com.alibaba.fastjson.JSON;
import com.edu.cqupt.shemining.common.FeatureType;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.service.FeatureManageService;
import com.edu.cqupt.shemining.vo.FeatureListVo;
import com.edu.cqupt.shemining.vo.FeatureVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "3.公共模块纳排——得到特征")
@RestController
@RequestMapping("/api/feature")
public class FgongController {

    @Autowired
    FeatureManageService featureManageService;

    private final ApplicationContext context;

    public FgongController(ApplicationContext context) {
        this.context = context;
    }

    //    @Autowired
//    public FeatureController(ApplicationContext context) {
//
//        this.context = context;
//    }
    //得到公共部分的特征
    @GetMapping("/getFeatures")
    public Result<List<FeatureVo>> getFeture(@RequestParam("index") Integer belongType){ // belongType说明是属于诊断类型、检查类型、病理类型、生命特征类型
        String type = null;
        for (FeatureType value : FeatureType.values()) {
            if(value.getCode() == belongType){
                type = value.getName();
            }
        }
        List<FeatureVo> list = featureManageService.getFeatureList(type);
        return Result.success(list);
    }


    //特征选择处使用接口



    // TODO 废弃方法
    @PostMapping("/insertFeature") // 上传特征分类结果
    public Result fieldInsert(@RequestBody FeatureListVo featureListVo){
        System.out.println("tableHeaders:"+ JSON.toJSONString(featureListVo));

        featureManageService.insertFeatures(featureListVo);
        return null;
    }
}
