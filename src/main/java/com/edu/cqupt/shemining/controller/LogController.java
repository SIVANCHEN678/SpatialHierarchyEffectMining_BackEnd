package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.AutoLog;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Log;
import com.edu.cqupt.shemining.model.LogEntity;
import com.edu.cqupt.shemining.model.Mining;
import com.edu.cqupt.shemining.model.QueryVo.LogQueryVo;
import com.edu.cqupt.shemining.model.QueryVo.MiningQueryVo;
import com.edu.cqupt.shemining.model.QueryVo.UserLogQueryVo;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.LogService;
import com.edu.cqupt.shemining.service.UserLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "admin——操作日志")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    public LogService logService;

    @Autowired
    private UserLogService userLogService;

    //4条件查询带分页
    @ApiOperation(value = "查询任务并分页")
    @GetMapping("/getLogByPage/{current}/{limit}")
    public Result getLogByPage(@PathVariable long current,
                               @PathVariable long limit,
                               @RequestParam(required = false) String userName){

        //创建page对象，传递当前页，每页记录数
        Page<UserLog> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<UserLog> wrapper = new QueryWrapper<>();

        if (userName != null){
            wrapper.like("user_name", userName);
        }


        Page<UserLog> page1 = userLogService.page(page, wrapper);
        return Result.success(page1);
    }

//    @GetMapping("/getLogByPage")
//    public Result queryLogByPage(@RequestParam Integer pageNum,
//                                 @RequestParam Integer pageSize,
//                                 @RequestParam String username
//    ){
//        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<UserLog>().orderByDesc("id");
//        queryWrapper.like(StringUtils.isNotBlank(username),"user_name",username);
//
//        Page<UserLog> page = userLogService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        return Result.success(page);
//    }

    @ApiOperation(value = "新增日志信息")
    @PostMapping
    public Result save(@RequestBody LogEntity log){
        logService.save(log);
        return Result.success(200,"成功");
    }

    @ApiOperation(value = "删除")
    @GetMapping("/delete/{id}")
    public boolean delete(@PathVariable int id){
        return logService.removeById(id);
    }

    //3条件查询带分页
    @ApiOperation(value = "条件分页")
    @PostMapping("/findLog/{current}/{limit}")
    public Result findTables(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) LogQueryVo logQueryVo){
        //创建page对象，传递当前页，每页记录数
        Page<LogEntity> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<LogEntity> wrapper = new QueryWrapper<>();
        String name = logQueryVo.getName();
        String username = logQueryVo.getUserName();
        if (name == null){
            wrapper.like("name",logQueryVo.getName());
        }
        if (name == null){
            wrapper.like("user_name",logQueryVo.getUserName());
        }

        //调用方法实现分页查询
        Page<LogEntity> page1 = logService.page(page, wrapper);

        return Result.success(page1);
    }
}
