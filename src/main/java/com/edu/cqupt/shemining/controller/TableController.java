package com.edu.cqupt.shemining.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Feature;
import com.edu.cqupt.shemining.model.QueryVo.TableQueryVo;
import com.edu.cqupt.shemining.model.Table;
import com.edu.cqupt.shemining.service.FeatureService;
import com.edu.cqupt.shemining.service.FileService;
import com.edu.cqupt.shemining.service.TableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Api(tags = "TODO废弃——数据表管理")
@RestController
@RequestMapping("/api/table")
@CrossOrigin
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FeatureService featureService;

    //1查询所有信息
    @ApiOperation(value = "查询所有表格信息")
    @GetMapping("/getAll")
    public List<Table> getAll(){
        return tableService.list();
    }

//    //2逻辑删除
//    @ApiOperation(value = "逻辑删除")
//    @DeleteMapping("/logisticDeleteById/{id}")
//    public Result logisticDeleteById(@PathVariable long id){
//        boolean flage = tableService.removeById(id);
//        if(flage){
//            return Result.success();
//        }else {
//            return Result.fail();
//        }
//    }
    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    @ApiOperation(value = "上传数据")
    @PostMapping("/update")
    @AutoLog("上传数据")
    public Result tableUpdate(@RequestBody Table table){
        log.info("已经进入到切面");
        boolean b = tableService.updateById(table);
        if (b){
            return Result.success(200, "上传数据成功");
        }else {
            return Result.fail(201, "上传数据失败");
        }
    }

    @ApiOperation(value = "新增数据")
    @PostMapping("/insert")
    @AutoLog("新增数据")
    public Result tableInsert(@RequestBody Table table){
        String time = DateUtil.now();
        table.setTime(time);
        boolean save = tableService.save(table);
        if (save){
            return Result.success(200, "上传数据成功");
        }else {
            return Result.fail(201, "上传数据失败");
        }
    }

    @ApiOperation(value = "数据表查询")
    @PostMapping("/search")
    public List<Table> search(@RequestBody(required = false)TableQueryVo tableQueryVo){
        QueryWrapper<Table> wrapper = new QueryWrapper<>();
        String diseaseName = tableQueryVo.getDiseaseName();
        String tableType = tableQueryVo.getTableType();
        if (diseaseName == null){
            wrapper.like("disease_name",tableQueryVo.getDiseaseName());
        }
        if (tableType == null){
            wrapper.like("table_type",tableQueryVo.getTableType());
        }

        List<Table> list = tableService.list(wrapper);
        return list;

    }


    @ApiOperation(value = "删除数据")
    @GetMapping("/delete/{tableId}")
    @AutoLog("删除数据")
    public Result delete(@PathVariable int tableId){
        boolean b = tableService.removeById(tableId);
        if (b){
            return Result.success(200, "上传数据成功");
        }else {
            return Result.fail(201, "上传数据失败");
        }
    }

    //3条件查询带分页
    @ApiOperation(value = "查询数据")
    @PostMapping("/findTables/{current}/{limit}")
    @AutoLog("查询数据")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) TableQueryVo tableQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<Table> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Table> wrapper = new QueryWrapper<>();
        String diseaseName = tableQueryVo.getDiseaseName();
        String tableType = tableQueryVo.getTableType();
        if (diseaseName == null){
            wrapper.like("disease_name",tableQueryVo.getDiseaseName());
        }
        if (tableType == null){
            wrapper.like("table_type",tableQueryVo.getTableType());
        }

        //调用方法实现分页查询
        Page<Table> page1 = tableService.page(page, wrapper);

        return Result.success(page1);
    }

    //4修改数据表
    @ApiOperation(value = "修改数据")
    @PostMapping("/updateTable")
    @AutoLog("修改数据")
    public Result updateTable(@RequestBody Table table){
        boolean flage = tableService.updateById(table);
        if (flage){
            return Result.success(200, "上传数据成功");
        }else {
            return Result.fail(201, "上传数据失败");
        }
    }



    @ApiOperation(value = "导入数据")
    @PostMapping("/upload")
    @AutoLog("导入数据")
    public Result uploadFile(@RequestPart("file") MultipartFile file){
        try {
            return Result.success(fileService.fileUpload(file));
        } catch (Exception e) {
            System.out.println(e);
            return Result.fail(201, "上传数据失败");
        }
    }

    @ApiOperation(value = "根据表名返回表头")
    @GetMapping("/getHeader/{tableName}")
    public Result getHeader(@PathVariable String tableName){
        List<String> columnName = tableService.getColumnName(tableName);
        return Result.success(columnName);
    }

    @ApiOperation(value = "新增特征")
    @PostMapping("/addFeatures")
    @AutoLog("新增特征")
    public Result addFeatures(@RequestBody Feature feature){
        String diseaseName = feature.getDiseaseName();
        QueryWrapper<Feature> wrapper = new QueryWrapper<>();
        wrapper.like("disease_name",diseaseName);
        List<Feature> list = featureService.list(wrapper);

        boolean flag = true;
        for (int i=0; i<list.size(); i++){
            if (Objects.equals(list.get(i).getFeatureName(), feature.getFeatureName())){
                flag = false;
            }
        }
        if (flag){
            return Result.success(featureService.save(feature));
        }else {
            return Result.fail(201, "特征已经存在");
        }
    }
}
