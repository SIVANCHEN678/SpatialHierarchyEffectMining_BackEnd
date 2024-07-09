package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Disease;
import com.edu.cqupt.shemining.model.KnowledgeGraph;
import com.edu.cqupt.shemining.service.DiseaseService;
import com.edu.cqupt.shemining.service.KnowledgeGraphService;
import com.edu.cqupt.shemining.vo.CausalRelationVo;
import com.edu.cqupt.shemining.vo.RequstFormVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.x.protobuf.MysqlxSession;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Api(tags = "4.因果知识图谱")
@RestController
@RequestMapping("/api/knowledgeGraph")
public class KnowledgeGraphController {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @Autowired
    private DiseaseService diseaseService;

    @ApiOperation(value = "获取疾病名称成功")
    @GetMapping("/getKGDiseases/{uid}")
    public Result getKGDiseases(@PathVariable Integer uid) {
        List<Disease> list = diseaseService.list(new QueryWrapper<Disease>().eq("uid", uid));
        ArrayList<String> diseases = new ArrayList<>();
        for (Disease item : list){
            diseases.add(item.getName());
        }
        return Result.success(200, "获取疾病名称成功", diseases);
    }


    @ApiOperation(value = "保存因果关系数组")
    @PostMapping("saveCausalityArray")
    public Result saveCausalityArray(@RequestBody RequstFormVo requstFormVo) {

        // 保存第一层因果关系
        Result resultFirstLayer = saveFirstLayer(requstFormVo);
        if (resultFirstLayer.getCode() != 200) {
            return resultFirstLayer;
        }

        // 保存第二层因果关系
        Result resultSecondLayer = saveSecondLayer(requstFormVo);
        if (resultSecondLayer.getCode() != 200) {
            return resultSecondLayer;
        }

        return Result.success(200, "因果关系保存成功");
    }

    private Result saveFirstLayer(RequstFormVo requstFormVo) {
        String context = requstFormVo.getStringFactor();
        String diseaseName = requstFormVo.getDiseaseName();
        Integer uid = requstFormVo.getUid();

        List<Disease> list1 = diseaseService.list(new QueryWrapper<Disease>().eq("uid", uid));

        Integer maxId = 0;
        Boolean exist = false;
        Integer diseaseId = 0;
        for (Disease item : list1) {
            if (item.getDiseaseId() > maxId) {
                maxId = item.getDiseaseId();
            }
            if (item.getName().equals(diseaseName)) {
                exist = true;
                diseaseId = item.getDiseaseId();
            }
        }

        if (!exist) {
            diseaseId = maxId + 10000;
            Disease disease2 = new Disease();
            disease2.setDiseaseId(diseaseId);
            disease2.setName(diseaseName);
            disease2.setUid(uid);
            boolean save = diseaseService.save(disease2);
        }

//        查找每种疾病因素最大id
        Integer maxDiseaseChildrenId = 0;
        Integer childreId = diseaseId * 10;
        List<KnowledgeGraph> list = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("parent_id", diseaseId).eq("uid", uid));

        if (!list.isEmpty()){
            for (KnowledgeGraph item : list) {
                if (item.getChildrenId() > maxDiseaseChildrenId){
                    maxDiseaseChildrenId = item.getChildrenId();
                }
            }
            childreId = maxDiseaseChildrenId;
        }


        String[] split = context.split(",");

        for (int i = 0; i < split.length; i++) {
            childreId++;
            KnowledgeGraph one = knowledgeGraphService.getOne(new QueryWrapper<KnowledgeGraph>()
                    .eq("parent_name", diseaseName)
                    .eq("children_name", split[i])
                    .eq("uid", uid));
            if (one == null) {
                KnowledgeGraph knowledgeGraph = new KnowledgeGraph();
                knowledgeGraph.setParentId(diseaseId);
                knowledgeGraph.setParentName(diseaseName);
                knowledgeGraph.setChildrenId(childreId);
                knowledgeGraph.setChildrenName(split[i]);
                knowledgeGraph.setUid(uid);
                knowledgeGraph.setDiseaseName(diseaseName);
                knowledgeGraphService.save(knowledgeGraph);
            }
        }
        return Result.success(200, "第一层保存成功");
    }

    private Result saveSecondLayer(RequstFormVo requstFormVo) {
        Integer uid = requstFormVo.getUid();
        String diseaseName = requstFormVo.getDiseaseName();

        String[] splitFactor = requstFormVo.getStringFactor().split(",");
        for (int i = 0; i < splitFactor.length; i++) {
            KnowledgeGraph knowledgeGraph = knowledgeGraphService.getOne(new QueryWrapper<KnowledgeGraph>()
                    .eq("children_name", splitFactor[i])
                    .eq("parent_name", requstFormVo.getDiseaseName())
                    .eq("uid", uid));

            Integer childrenId = knowledgeGraph.getChildrenId();
            String childrenName = knowledgeGraph.getChildrenName();

            for (int j = 0; j < requstFormVo.getListStr().size(); j++) {
                String[] splitRelation = requstFormVo.getListStr().get(j).split(" -> ");

                // 检查数据库中是否已存在相同的因果关系
                KnowledgeGraph existingRelation = knowledgeGraphService.getOne(new QueryWrapper<KnowledgeGraph>()
                        .eq("parent_name", childrenName)
                        .eq("children_name", splitRelation[1])
                        .eq("uid", uid).eq("disease_name", diseaseName));

                // 如果已存在相同的因果关系，则跳过保存
                if (existingRelation != null) {
                    continue;
                }

                if (Objects.equals(splitFactor[i], splitRelation[0])) {
                    KnowledgeGraph knowledgeGraph1 = new KnowledgeGraph();
                    KnowledgeGraph one = knowledgeGraphService.getOne(new QueryWrapper<KnowledgeGraph>()
                            .eq("parent_name", diseaseName)
                            .eq("children_name", splitRelation[1])
                            .eq("uid", uid));

                    knowledgeGraph1.setParentName(splitFactor[i]);
                    knowledgeGraph1.setParentId(childrenId);
                    knowledgeGraph1.setChildrenId(one.getChildrenId());
                    knowledgeGraph1.setChildrenName(splitRelation[1]);
                    knowledgeGraph1.setUid(uid);
                    knowledgeGraph1.setDiseaseName(requstFormVo.getDiseaseName());
                    knowledgeGraphService.save(knowledgeGraph1);
                }
            }
        }
        return Result.success(200, "第二层保存成功");
    }



    @ApiOperation(value = "！！！得到知识图谱Json格式")
    @GetMapping("/getKnowledgeGraph/{uid}")
    public Result getKnowledgeGraph(@PathVariable Integer uid) {
        QueryWrapper<Disease> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        List<Disease> allDiseaseList = diseaseService.list(wrapper);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (Disease disease : allDiseaseList) {
            ObjectNode diseaseNode = mapper.createObjectNode();
            diseaseNode.put("id", disease.getDiseaseId());
            diseaseNode.put("name", disease.getName());

            List<KnowledgeGraph> kgList = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().
                    eq("parent_id", disease.getDiseaseId()).eq("uid", uid));
            if (!kgList.isEmpty()) {
                ArrayNode childrenArray = mapper.createArrayNode();
                for (KnowledgeGraph kg : kgList) {
                    ObjectNode childNode = mapper.createObjectNode();
                    childNode.put("id", kg.getChildrenId());
                    childNode.put("name", kg.getChildrenName());
                    childNode.put("categary", "危险因素");

                    // 添加子节点的处理逻辑
                    // 如果有更多层级的子节点，可以继续按照上面的方式添加

                    childrenArray.add(childNode);

//                    读取第二层

                    List<KnowledgeGraph> list = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("parent_id", kg.getChildrenId())
                            .eq("parent_name", kg.getChildrenName()));
                    if (!list.isEmpty()){
                        int randomNumber = kg.getChildrenId()*100 +1;
                        ArrayNode arrayNode = mapper.createArrayNode();
                        for (KnowledgeGraph kgSecond: list){
                            ObjectNode secondNode = mapper.createObjectNode();
                            secondNode.put("id", randomNumber);
                            secondNode.put("name", kgSecond.getChildrenName());
                            secondNode.put("categary", "因果关系");

                            // 添加子节点的处理逻辑
                            // 如果有更多层级的子节点，可以继续按照上面的方式添加

                            arrayNode.add(secondNode);
                            randomNumber++;
                        }
                        childNode.set("children", arrayNode);
                    }
                }
                diseaseNode.set("children", childrenArray);
            }
            jsonArray.add(diseaseNode);
        }
        return Result.success(jsonArray);
    }

    @ApiOperation(value = "根据id删除知识图谱")
    @GetMapping("/deleteKnowledgeGraph/{id}")
    public Result deleteKnowledgeGraph(@PathVariable Integer id) {
        boolean b = knowledgeGraphService.removeById(id);
        return Result.success(b);
    }

    @ApiOperation(value = "得到所有知识图谱")
    @GetMapping("/getAllKnowledgeGraph/{user_id}")
    public Result getAllKnowledgeGraph(@PathVariable Integer user_id){
        QueryWrapper<KnowledgeGraph> user_id1 = new QueryWrapper<KnowledgeGraph>().eq("uid", user_id);
        return Result.success(knowledgeGraphService.list(user_id1));
    }

    @ApiOperation(value = "根据疾病查看知识图谱")
    @GetMapping("/searchKnowledgeGraph/{diseaseName}")
    public Result searchKnowledgeGraph(@PathVariable String diseaseName) {
        Disease disease = diseaseService.getOne(new QueryWrapper<Disease>().eq("name", diseaseName));
        List<KnowledgeGraph> knowledgeGraphList = new ArrayList<>();
        List<KnowledgeGraph> kgList = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("parent_id", disease.getId()));
        if (!kgList.isEmpty()) {
            for (KnowledgeGraph kg : kgList) {
                List<KnowledgeGraph> relation = knowledgeGraphService.list(new QueryWrapper<KnowledgeGraph>().eq("parent_id", kg.getChildrenId()));
                knowledgeGraphList.addAll(relation);
            }
        }
        return Result.success(knowledgeGraphList);
    }

    @ApiOperation(value = "根据病种进行分页查询")
    @PostMapping ("/findKnowledgeGraph/{current}/{limit}/{uid}")
    public Result findKnowledgeGraph(@PathVariable long current,
                                        @PathVariable long limit,
                                        @PathVariable Integer uid,
                                        @RequestBody(required = false) CausalRelationVo causalRelationVo){
        //创建page对象，传递当前页，每页记录数
        Page<KnowledgeGraph> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<KnowledgeGraph> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        if (causalRelationVo.getDiseaseName() != null){
            System.out.println("KKKKKKKKKKK");
            wrapper.eq("disease_name", causalRelationVo.getDiseaseName());
        }
        //调用方法实现分页查询
        Page<KnowledgeGraph> page1 = knowledgeGraphService.page(page, wrapper);

        return Result.success(page1);
    }

}
