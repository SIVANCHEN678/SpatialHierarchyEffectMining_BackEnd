package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.NoticeMapper;
import com.edu.cqupt.shemining.model.Notification;
import com.edu.cqupt.shemining.service.NoticeService;
import com.edu.cqupt.shemining.vo.InsertNoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notification>
        implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public void saveNotification(InsertNoticeVo notification) {
        noticeMapper.saveNotification(notification);
    }

    @Override
    public List<Notification> queryNotices() {

        List<Notification> notifications = noticeMapper.selectList(null);
        return notifications;
    }
}
