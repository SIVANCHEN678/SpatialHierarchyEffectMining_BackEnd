package com.edu.cqupt.shemining.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.FeatureType;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Feature;
import com.edu.cqupt.shemining.model.FieldManagementEntity;
import com.edu.cqupt.shemining.model.QueryVo.FeatureQueryVo;
import com.edu.cqupt.shemining.service.FeatureManageService;
import com.edu.cqupt.shemining.service.FeatureService;
import com.edu.cqupt.shemining.service.FieldManagementService;
import com.edu.cqupt.shemining.service.TableService;
import com.edu.cqupt.shemining.vo.FeatureListVo;
import com.edu.cqupt.shemining.vo.FeatureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "3.admin——特征管理")
@RestController
@RequestMapping("/admin/feature")
public class FeatureController {

    @Autowired
    public FeatureService featureService;

    @Autowired
    public TableService tableService;
    private final ApplicationContext context;

    @Autowired
    public FieldManagementService fieldManagementService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FeatureController(ApplicationContext context) {
        this.context = context;
    }

    @ApiOperation(value = "得到所有特征")
    @GetMapping("/getAllFeatures")
    public List<Feature> getAllFeatures(){
        return featureService.list();
    }



    @ApiOperation(value = "插入特征")
    @PostMapping("/insertFeatures")
    @AutoLog("插入特征")
    public Result insertFeatures(@RequestBody Feature feature){
        boolean save = featureService.save(feature);
        if (save){
            return Result.success(200,"插入成功");
        }else {
            return Result.fail(201,"插入失敗");
        }
    }

    @ApiOperation(value = "更新：根据公共表名返回表头")
    @GetMapping("/getHeader/{tableName}")
    public Result getHeader(@PathVariable String tableName){
        List<String> columnName = fieldManagementService.getColumnName(tableName);
        return Result.success(columnName);
    }

    @ApiOperation(value = "更新：往公共数据集中插入特征插入特征")
    @PostMapping("/insertPublicFeatures")
    @AutoLog("插入特征")
    public Result insertPublicFeatures(@RequestBody FieldManagementEntity fieldManagementEntity){
        boolean save = fieldManagementService.save(fieldManagementEntity);
        if (save){
            return Result.success(200,"插入成功");
        }else {
            return Result.fail(201,"失敗");
        }
    }

    @ApiOperation(value = "特征缺失")
    @GetMapping("getMissingRates/{tableName}")
    public Map<String, Integer> getFillRates(@PathVariable("tableName") String tableName) {
        Map<String, Integer> fillRates = new HashMap<>();
        int totalRows = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM "   + tableName, Integer.class);

        // 获取特征列的名称
        List<String> featureNames = jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_name = ? ",
                String.class, tableName);

        // 对于每个特征，计算其填充率
        for (String feature : featureNames) {
            int missingCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM "   + tableName + " WHERE \"" + feature + "\" IS NULL",
                    Integer.class);
            double fillRate = 100.0 - ((double) missingCount / totalRows * 100.0);
            fillRates.put(feature, (int) fillRate); // 注意：这里可能会丢失精度，取决于实际情况是否接受四舍五入误差
        }
        return fillRates;
    }

    @ApiOperation(value = "删除特征")
    @GetMapping("/deleteFeature/{id}")
    @AutoLog("删除特征")
    public Result deleteFeature(@PathVariable Long id){
        boolean b = featureService.removeById(id);
        if (b){
            return Result.success(200,"删除成功");
        }else {
            return Result.fail(201,"删除失敗");
        }
    }

    //4修改数据表
    @ApiOperation(value = "修改特征")
    @PostMapping("/updateFeature")
    @AutoLog("修改特征")
    public Result updateFeature(@RequestBody Feature feature){
        boolean flage = featureService.updateById(feature);
        if (flage){
            return Result.success(200,"修改成功");
        }else {
            return Result.fail(201,"修改失敗");
        }
    }

    //3条件查询带分页
    @ApiOperation(value = "查询特征")
    @PostMapping("/findFeatures/{current}/{limit}")
    @AutoLog("查询特征")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) FeatureQueryVo featureQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<Feature> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Feature> wrapper = new QueryWrapper<>();
        String diseaseName = featureQueryVo.getDiseaseName();
        if (diseaseName != null){
            wrapper.like("disease_name",featureQueryVo.getDiseaseName());
        }

        //调用方法实现分页查询
        Page<Feature> page1 = featureService.page(page, wrapper);

        return Result.success(page1);
    }

    @Autowired
    FeatureManageService featureManageService;
    @GetMapping("/getFeatures")
    public Result getFeture(@RequestParam("index") Integer belongType){ // belongType说明是属于诊断类型、检查类型、病理类型、生命特征类型
        String type = null;
        for (FeatureType value : FeatureType.values()) {
            if(value.getCode() == belongType){
                type = value.getName();
            }
        }
        List<FeatureVo> list = featureManageService.getFeatureList(type);
        return Result.success(list);
    }


    // TODO 废弃方法
    @PostMapping("/insertFeature") // 上传特征分类结果
    public Result fieldInsert(@RequestBody FeatureListVo featureListVo){
        System.out.println("tableHeaders:"+ JSON.toJSONString(featureListVo));

        featureManageService.insertFeatures(featureListVo);
        return null;
    }
}
