package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.*;
import com.edu.cqupt.shemining.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "首页")
@RestController
@RequestMapping("/api/index")
public class IndexController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @Autowired
    private TableDescribeService tableDescribeService;



    @ApiOperation(value = "新！！查询因果关系总数")
    @GetMapping ("/getKnowledgeGraphs/{userId}")
    public Result getKnowledgeGraphs(@PathVariable Integer userId){
        ArrayList<String> strings = new ArrayList<>();
        List<Disease> diseaseList = diseaseService.list(new QueryWrapper<Disease>().eq("uid", userId));
        for (Disease disease: diseaseList){
            strings.add(disease.getName());
        }
        if (diseaseList.size() != 0){
            int count = knowledgeGraphService.count(new QueryWrapper<KnowledgeGraph>().eq("uid", userId).
                    notIn("parent_name", strings));
            return Result.success(200,"成功", count);
        }
        return Result.fail(200,"失败");
    }

    @ApiOperation(value = "新！！查询数据表总数")
    @GetMapping ("/getTables/{userId}")
    public Result getTables(@PathVariable Integer userId){
        int count = tableDescribeService.count(new QueryWrapper<TableDescribeEntity>().eq("uid", userId));
        return Result.success(200,"成功", count);
    }

    @ApiOperation(value = "新！！查询任务总数")
    @GetMapping ("/getMinings/{userId}")
    public Result getMinings(@PathVariable Integer userId){
        int count = miningService.count(new QueryWrapper<Mining>().eq("uid", userId));
        return Result.success(200,"成功", count);
    }


    @ApiOperation(value = "新！！查询各个疾病因果关系总数")
//    @GetMapping ("/getKnowledgeGraphsByDisease/{userId}")
//    public Result getKnowledgeGraphsByDisease(@PathVariable Integer userId){
//        HashMap<String, Integer> maps = new HashMap<>();
//        List<Disease> user_id = diseaseService.list(new QueryWrapper<Disease>().eq("uid", userId));
//        for (Disease disease: user_id){
//            ArrayList<Integer> childrenList = new ArrayList<>();
//            List<KnowledgeGraph> list = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("uid", userId).
//                    eq("parent_name", disease.getName()));
//            if (list.size() == 0){
//                continue;
//            }
//            for (KnowledgeGraph index: list){
//                childrenList.add(index.getChildrenId());
//            }
//            int count = knowledgeGraphService.count(new QueryWrapper<KnowledgeGraph>().eq("uid", userId).in("parent_id", childrenList));
//            String name = disease.getName();
//            maps.put(name, count);
//        }
//        return Result.success(maps);
//    }
    @GetMapping("/getKnowledgeGraphsByDisease/{userId}")
    public Result getKnowledgeGraphsByDisease(@PathVariable Integer userId) {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Disease> user_id = diseaseService.list(new QueryWrapper<Disease>().eq("uid", userId));
        for (Disease disease : user_id) {
            ArrayList<Integer> childrenList = new ArrayList<>();
            List<KnowledgeGraph> list = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("uid", userId)
                    .eq("parent_name", disease.getName()));
            if (list.size() == 0) {
                continue;
            }
            for (KnowledgeGraph index : list) {
                childrenList.add(index.getChildrenId());
            }
            int count = knowledgeGraphService.count(new QueryWrapper<KnowledgeGraph>().eq("uid", userId).in("parent_id", childrenList));
            Map<String, Object> item = new HashMap<>();
            item.put("value", count);
            item.put("name", disease.getName());
            data.add(item);
        }
        return Result.success(data);
    }

    @ApiOperation(value = "新！！查询挖疾病list")
    @GetMapping ("/getDiseaseList/{userId}")
    @AutoLog("查询任务")
    public Result getDiseaseList(@PathVariable Integer userId){
        List<String> diseaseList = new ArrayList<>();
        List<Disease> user_id = diseaseService.list(new QueryWrapper<Disease>().eq("uid", userId));
        for (Disease disease: user_id){
            diseaseList.add(disease.getName());
        }
        return Result.success(diseaseList);
    }

    @ApiOperation(value = "新！！根据病种得到数据")
    @GetMapping("/getTablesByDisease/{userId}")
    @AutoLog("查询任务")
    public Result getTablesByDisease(@PathVariable Integer userId) {
        List<List<Object>> data = new ArrayList<>();
        Set<String> uniqueStrings = new HashSet<>();
        List<Object> daysOfWeek = new ArrayList<>();
        List<Object> counts = new ArrayList<>();
        List<TableDescribeEntity> user_id = tableDescribeService.list(new QueryWrapper<TableDescribeEntity>().eq("uid", userId));
        for (TableDescribeEntity item : user_id){
            uniqueStrings.add(item.getDiseaseName());
        }
        // 创建一个数组来存储不重复的字符串
        String[] uniqueArray = new String[uniqueStrings.size()];

        // 将 Set 中的元素复制到数组中
        uniqueStrings.toArray(uniqueArray);

        // 打印不重复的字符串数组
        for (String str : uniqueArray) {
            System.out.println(str);
            int count = tableDescribeService.count(new QueryWrapper<TableDescribeEntity>().eq("disease_name", str).eq("uid", userId));
            daysOfWeek.add(str);
            counts.add(count);
        }
        data.add(daysOfWeek);
        data.add(counts);
        return Result.success(data);
    }


    @Autowired
    private TableService tableService;

    @Autowired
    private CausalRelationshipsService causalRelationshipsService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private MiningService miningService;

    @ApiOperation(value = "查因果关系量")
    @GetMapping("/getCausalRelationshipsNum")
    public Integer getCausalRelationships(){
        int count = causalRelationshipsService.count();
        return count;
    }

    @ApiOperation(value = "查数据量")
    @GetMapping("/getDataNum")
    public Integer getDataAcount(){
        int count = tableService.count();
        return count;
    }

    @ApiOperation(value = "查算法个数")
    @GetMapping("/getAlgorithmNum")
    public Integer getAlgorithmNum() {
        int count = algorithmService.count();
        return count;
    }

    @ApiOperation(value = "查询所有Mining个数")
    @GetMapping("/getMiningNum")
    public Integer getMiningNum(){
        int count = miningService.count();
        return count;
    }

    @ApiOperation(value = "查询所有因果关系")
    @PostMapping("/getAllRelationships")
    public List<CausalRelationships> getAllRelationships(){
        List<CausalRelationships> list = causalRelationshipsService.list();
        return list;
    }

    @ApiOperation(value = "查数据")
    @PostMapping("/getData")
    public List<Table> getData(){
        String wpString = ".prior";
        QueryWrapper<Table> wrapper = new QueryWrapper<>();
        wrapper.notLike("table_name", wpString);
        List<Table> list = tableService.list(wrapper);
        return list;
    }
}
