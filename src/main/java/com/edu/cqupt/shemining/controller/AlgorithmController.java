package com.edu.cqupt.shemining.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Algorithm;
import com.edu.cqupt.shemining.model.QueryVo.AlgorithmQueryVo;
import com.edu.cqupt.shemining.service.AlgorithmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Api(tags = "2.admin——算法管理")
@RestController
@RequestMapping("/admin/algorithm")
public class AlgorithmController {

    @Autowired
    public AlgorithmService algorithmService;

    //1查询所有信息
    @ApiOperation(value = "查询所有算法信息")
    @GetMapping("/getAll/{dataType}")
    public List<Algorithm> findAllTables(@PathVariable(required = false) String dataType){
        if(dataType == null){
            List<Algorithm> list = algorithmService.list();
            return list;
        }
        List<Algorithm> list = algorithmService.list(new QueryWrapper<Algorithm>().like("data_type",dataType));
        return list;
    }

    //1查询所有信息
    @ApiOperation(value = "查询所有算法信息")
    @GetMapping("/getAllAlgorithm")
    public List<Algorithm> getAllAlgorithm(){
        List<Algorithm> list = algorithmService.list();
        return list;
    }

    @PostMapping("/list")
    public Object index(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize){
        System.out.println("pageNum:"+pageNum);
        System.out.println("pageSize:"+pageSize);

        Page<Algorithm> pg = new Page<>(pageNum,pageSize);
        IPage<Algorithm> algorithmIPage = algorithmService.page(pg);
        System.out.println("总条数 ------> [{}]"+algorithmIPage.getTotal());
        System.out.println("当前页数 ------> [{}]" + algorithmIPage.getCurrent());
        System.out.println("当前每页显示数 ------> [{}]" + algorithmIPage.getSize());
        System.out.println("当前页数据 ------> [{}]" + algorithmIPage.getRecords());
        return algorithmIPage;
    }

    @ApiOperation(value = "修改算法")
    @PostMapping("/update")
    @AutoLog("修改算法")
    public Result algorithmUpdate(@RequestBody Algorithm algorithm){
        boolean b = algorithmService.updateById(algorithm);
        if (b){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    @ApiOperation(value = "新增算法")
    @PostMapping("/insert")
    @AutoLog("新增算法")
    public Result algorithmInsert(@RequestBody Algorithm algorithm){
        String time = DateUtil.now();
        algorithm.setTime(time);
        boolean save = algorithmService.save(algorithm);
        if (save){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    @ApiOperation(value = "算法查询")
    @PostMapping("/search")
    @AutoLog("查询算法")
    public Result search(@RequestBody(required = false) AlgorithmQueryVo algorithmQueryVo){
        QueryWrapper<Algorithm> wrapper = new QueryWrapper<>();
        String algorithmName = algorithmQueryVo.getAlgorithmName();
        String algorithmType = algorithmQueryVo.getAlgorithmType();
        if (algorithmName == null){
            wrapper.like("algorithm_name",algorithmQueryVo.getAlgorithmName());
        }
        if (algorithmType == null){
            wrapper.like("algorithm_type",algorithmQueryVo.getAlgorithmType());
        }

        List<Algorithm> list = algorithmService.list(wrapper);
        return Result.success(list);

    }


    //2逻辑删除
    @ApiOperation(value = "删除算法")
    @GetMapping("/delete/{id}")
    @AutoLog("删除算法")
    public Result logisticDeleteById(@PathVariable Integer id){
        boolean flag = algorithmService.removeById(id);
        if (flag){
            return Result.success(200,"成功");
        }else {
            return Result.fail(201,"失败");
        }
    }

    //3条件查询带分页
    @ApiOperation(value = "查询算法")
    @PostMapping("/findTables/{current}/{limit}")
    @AutoLog("查询算法")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) AlgorithmQueryVo algorithmQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<Algorithm> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Algorithm> wrapper = new QueryWrapper<>();
        String disease = algorithmQueryVo.getAlgorithmName();
        String diseaseType = algorithmQueryVo.getAlgorithmType();
        if (disease == null){
            wrapper.like("algorithmName",algorithmQueryVo.getAlgorithmName());
        }
        if (diseaseType == null){
            wrapper.like("algorithmType",algorithmQueryVo.getAlgorithmType());
        }

        //调用方法实现分页查询
        Page<Algorithm> page1 = algorithmService.page(page, wrapper);

        return Result.success(page1);
    }

    @Value("${gorit.file.root.path1}")
    private String filePath;

    SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");

    // 日志打印
    private Logger log = LoggerFactory.getLogger("FileController");

    // 文件上传 （可以多文件上传）
    @ApiOperation(value = "上传算法")
    @PostMapping("/uploadAlgorithm")
    @AutoLog("上传算法")
    public Result fileUploads(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        // 得到格式化后的日期
        String format = sdf.format(new Date());
        // 获取上传的文件名称
        String fileName = file.getOriginalFilename();
        // 时间 和 日期拼接
        String newFileName = format + "_" + fileName;
        // 得到文件保存的位置以及新文件名
        File dest = new File(filePath + newFileName);
        try {
            // 上传的文件被保存了
            file.transferTo(dest);
            // 打印日志
            log.info("上传成功，当前上传的文件保存在 {}",filePath + newFileName);
            // 自定义返回的统一的 JSON 格式的数据，可以直接返回这个字符串也是可以的。
            return Result.success(200,"上传成功");
        } catch (IOException e) {
            log.error(e.toString());
        }
        // 待完成 —— 文件类型校验工作
        return Result.fail(201,"上传错误");
    }
}
