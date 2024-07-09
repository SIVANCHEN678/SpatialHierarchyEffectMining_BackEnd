package com.edu.cqupt.shemining.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Feature;
import com.edu.cqupt.shemining.model.Mining;
import com.edu.cqupt.shemining.model.QueryVo.MiningQueryVo;
import com.edu.cqupt.shemining.model.TableDescribeEntity;
import com.edu.cqupt.shemining.service.*;
import com.edu.cqupt.shemining.util.PythonRun;
import com.edu.cqupt.shemining.vo.CaualRelationVo;
import com.edu.cqupt.shemining.vo.DiffReturnVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.core.io.ClassPathResource;
@Api(tags = "5.算法运行")
@RestController
@RequestMapping("/api/mining")
public class MiningController {

    private String[] causalString = new String[]{};

    @Autowired
    public TableService tableService;

    @Autowired
    private TableDescribeService tableDescribeService;

    @Autowired
    public MiningService miningService;

    @Autowired
    public CausalRelationshipsService causalRelationshipsService;



    @Autowired
    public FeatureService featureService;

    @Resource
    private DataSourceProperties dataSourceProperties;

    @Resource
    private PythonRun pythonRun;

    //1查询所有信息
    @ApiOperation(value = "查询所有任务信息")
    @PostMapping("/getAll/{userId}")
    public List<Mining> findAllTables(@PathVariable Integer userId){
        QueryWrapper<Mining> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Mining> list = miningService.list(wrapper);
        return list;
    }
    @GetMapping("/api/document")
    public String getDocument() throws IOException {
        // 从类路径中读取固定文档
        ClassPathResource classPathResource = new ClassPathResource("C:\\Users\\hp-pc\\Desktop\\工具软件相关文档\\软件8-疾病危险因素空间层次效应挖掘.docx");
        InputStream inputStream = classPathResource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    //3删除
    @ApiOperation(value = "删除任务")
    @GetMapping("/delete/{id}")
    @AutoLog("删除任务")
    public Result delete(@PathVariable Integer id){
        boolean flag = miningService.removeById(id);
        if (flag){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    //4条件查询带分页
    @ApiOperation(value = "查询任务并分页")
    @PostMapping("/findMining/{current}/{limit}/{userId}")
    @AutoLog("查询任务")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @PathVariable Integer userId,
                                @RequestBody(required = false) MiningQueryVo miningQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<Mining> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Mining> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", userId);
        String diseaseName = miningQueryVo.getDiseaseName();
        String algorithmId = miningQueryVo.getAlgorithmId();
        String type = miningQueryVo.getType();
        String tableName = miningQueryVo.getTableName();
        if (type != null){
            wrapper.eq("type",miningQueryVo.getType());
        }
        if (diseaseName != null){
            wrapper.eq("disease_name",miningQueryVo.getDiseaseName());
        }
        if (algorithmId != null){
            wrapper.eq("algorithm_id",miningQueryVo.getAlgorithmId());
        }
        if (tableName != null){
            wrapper.eq("table_name",miningQueryVo.getTableName());
        }

        //调用方法实现分页查询
        Page<Mining> page1 = miningService.page(page, wrapper);

        return Result.success(page1);
    }

    //5增
    @ApiOperation(value = "创建任务")
    @PostMapping("/insert")
    @AutoLog("创建任务")
    public Result insert(@RequestBody Mining mining){
        String time = DateUtil.now();
        mining.setTime(time);
        boolean flag = miningService.save(mining);
        if (flag){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    @ApiOperation(value = "根据表名返回表头")
    @GetMapping("/getFeatures/{tableName}/{diseaseName}")
    public Result getFeatures(@PathVariable String tableName, @PathVariable String diseaseName){
        Integer region = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_name", tableName).eq("disease_name", diseaseName)).getRegion();

        List<String> columnName = tableService.getColumnName(tableName);
        List<Feature> list = new ArrayList<>();
        QueryWrapper<Feature> wrapper = new QueryWrapper<>();
        if (region == 1){
            wrapper.eq("disease_name", "私有数据");
            list = featureService.list(wrapper);
        }else {
            wrapper.eq("disease_name", diseaseName);
            list = featureService.list(wrapper);
        }

        Map<String, Feature> featureMap = new HashMap<>();
        for (int i = 0 ; i< columnName.size() ; i++){
            for (int j = 0 ; j< list.size() ; j++){
                if (Objects.equals(columnName.get(i), list.get(j).getFeatureName())){
                    featureMap.put(columnName.get(i), list.get(j));
                }
            }
        }
        System.out.println(featureMap);
        return Result.success(featureMap);
    }

    @Value("${gorit.file.root.path2}")
    private String imageFile;

    @ApiOperation(value = "得到图片")
    @GetMapping("/getImage/{name}")
    public String getImage(@PathVariable String name) {

        String imageString = "";
        String file = imageFile;
//        String file = imageFile;
        String path = file + name + ".png";
        try {

            FileInputStream fis = new FileInputStream(new File(path));
            int count = 0;
            while (count == 0) {

                count = fis.available();
            }
            byte[] read = new byte[count];
            System.err.println(read);
            fis.read(read);
            imageString = Base64.encodeBase64String(read);
//            System.err.println("返回前端的base64图片字符串:"+imageString);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return  imageString;
    }

    @Value("${gorit.file.root.path3}")
    private String algorithmFile;

    @Value("${gorit.file.root.path4}")
    private String algorithmFileWithout;

    @Value("${gorit.file.root.path2}")
    private String saveFile;


    @ApiOperation(value = "执行任务")
    @PostMapping("/runing")
    @AutoLog("执行任务")
    public Result runing(@RequestBody Mining mining) throws Exception {
        System.out.println(mining);
        String algorithmFilePath = algorithmFile;

        List<String> args = new LinkedList<>();
        args.add("--algorithm-id="+mining.getAlgorithmId());
        args.add("--table-name="+mining.getTableName());
        args.add("--data-type="+mining.getDataType());
        args.add("--score-id="+mining.getScoreId());
        args.add("--max-degree="+mining.getMaxDegree());

        args.add("--living-habit="+mining.getLivingHabit());
        args.add("--social-connection="+mining.getSocialConnection());
        args.add("--clinical-representation="+mining.getClinicalRepresentation());
        args.add("--save-path="+saveFile);

        args.add("--province="+mining.getProvince());
        args.add("--city="+mining.getCity());
        args.add("--county="+mining.getCounty());

        String grahp = pythonRun.run(algorithmFilePath,args);
        causalString = grahp.split(",");
        String[] split = grahp.split(",");
        System.out.println(split);
        return Result.success(split);
    }

    @ApiOperation(value = "执行任务")
    @PostMapping("/runing1")
    @AutoLog("执行任务")
    public Result runing1(@RequestBody Mining mining) throws Exception {
        System.out.println(mining);
        String algorithmFilePath = algorithmFileWithout;

        List<String> args = new LinkedList<>();
        args.add("--algorithm-id="+mining.getAlgorithmId());
        args.add("--table-name="+mining.getTableName());
        args.add("--data-type="+mining.getDataType());
        args.add("--score-id="+mining.getScoreId());
        args.add("--max-degree="+mining.getMaxDegree());

        args.add("--living-habit="+mining.getLivingHabit());
        args.add("--social-connection="+mining.getSocialConnection());
        args.add("--clinical-representation="+mining.getClinicalRepresentation());
        args.add("--save-path="+saveFile);

        args.add("--province="+mining.getProvince());
        args.add("--city="+mining.getCity());
        args.add("--county="+mining.getCounty());

        String grahp = pythonRun.run(algorithmFilePath,args);
        causalString = grahp.split(",");
        String[] split = grahp.split(",");
        System.out.println(split);
        return Result.success(split);
    }

    @ApiOperation(value = "4.11——结果饼状图")
    @PostMapping("/getPie")
    public Result getPie(@RequestBody CaualRelationVo caualRelationVo){
        int[] newResult = new int[3];
        Arrays.fill(newResult, 0);

        List<String> causalRelations = caualRelationVo.getCausalRelationships();
        System.out.println("!!!!!!!!!!");
        System.out.println(causalRelations);
        String livingHabit = caualRelationVo.getLivingHabit();
        String clinicalRepresentation = caualRelationVo.getClinicalRepresentation();
        String socialConnection = caualRelationVo.getSocialConnection();

        String[] split1 = livingHabit.split(",");
        String[] split2 = clinicalRepresentation.split(",");
        String[] split3 = socialConnection.split(",");


        for (String causal: causalRelations){
            String[] split = causal.split(" -> ");
            for (String lhStr: split1){
                if (split[0].equals(lhStr)){
                    newResult[1] +=1;
                }
            }
            for (String clStr: split2){
                if (split[0].equals(clStr)){
                    newResult[0] +=1;
                }
            }
            for (String lhStr: split3){
                if (split[0].equals(lhStr)){
                    newResult[2] +=1;
                }
            }
        }
        HashMap<String, Integer> resultMap = new HashMap<>();
        resultMap.put( "clinicalRepresentation", newResult[0]);
        resultMap.put("livingHabit", newResult[1]);
        resultMap.put("socialConnection", newResult[2]);
        System.out.println(resultMap);
        return Result.success(200,"成功",resultMap);
    }

    @ApiOperation(value = "4.29——得到两个结果的不同之处")
    @PostMapping("/getDifferent")
    public Result findTables(@RequestParam List<String> firstMining,
                             @RequestParam List<String> secondMining){
        HashSet<String> diffFirst = new HashSet<>(firstMining);
        HashSet<String> diffSecond = new HashSet<>(secondMining);

        diffFirst.removeAll(secondMining);
        diffSecond.removeAll(firstMining);

        DiffReturnVo diffReturnVo = new DiffReturnVo();
        ArrayList<String> firstStrings = new ArrayList<>();
        ArrayList<String> secondStrings = new ArrayList<>();

        for (String f: diffFirst){
            firstStrings.add(f);
            System.out.println(f);
        }
        for (String s: diffSecond){
            secondStrings.add(s);
            System.out.println(s);
        }
        diffReturnVo.setDiffFirst(firstStrings);
        diffReturnVo.setDiffSecond(secondStrings);
        return Result.success(200, "成功", diffReturnVo);
    }



}
