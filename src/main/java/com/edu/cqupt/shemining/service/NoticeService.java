package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.Notification;
import com.edu.cqupt.shemining.vo.InsertNoticeVo;

import java.util.List;

public interface NoticeService extends IService<Notification> {

    void saveNotification(InsertNoticeVo notification);

    List<Notification> queryNotices();

}