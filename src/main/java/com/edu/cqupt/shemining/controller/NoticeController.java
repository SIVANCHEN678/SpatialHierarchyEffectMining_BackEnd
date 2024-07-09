package com.edu.cqupt.shemining.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.Notification;
import com.edu.cqupt.shemining.model.User;
import com.edu.cqupt.shemining.model.UserLog;
import com.edu.cqupt.shemining.service.NoticeService;
import com.edu.cqupt.shemining.vo.InsertNoticeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(tags = "5.公告信息")
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

//    @GetMapping("/allNotices")
//    public Result allNotices(@RequestParam Integer current , @RequestParam Integer limit){
//        Page<Notification> page = new Page<>(current, limit);
//
//        return Result.success(page);
////        return noticeService.allNotices(pageNum, pageSize);
//    }

    @ApiOperation(value = "分页——所有通知信息")
    @GetMapping("/allNotices")
    public Result allNotices(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int limit) {
        Page<Notification> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();

        Page<Notification> page1 = noticeService.page(page, wrapper);


        return Result.success(page1);
    }


        @GetMapping("/queryNotices")
    public List<Notification> queryNotices(){
        return noticeService.queryNotices();
    }




    @PostMapping("/updateNotice")
    public Result updateNotice(@RequestBody Notification notification){

        notification.setUpdateTime(new Date());
        noticeService.saveOrUpdate(notification);

        return Result.success(200,"成功");
    }


    @PostMapping("delNotice")
    public Result delNotice(@RequestBody Notification notification){
        noticeService.removeById(notification.getInfoId());
        return Result.success(200,"成功");
    }

    @PostMapping("insertNotice")
    public Result insertNotice(@RequestBody InsertNoticeVo notification){

        noticeService.saveNotification(notification);
        return Result.success(200,"成功");
    }


}

