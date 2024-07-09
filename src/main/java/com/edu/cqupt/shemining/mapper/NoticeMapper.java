package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.Notification;
import com.edu.cqupt.shemining.vo.InsertNoticeVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseMapper<Notification> {

    void saveNotification(InsertNoticeVo notification);

    List<Notification> queryNotices();
    List<Notification> selectAllNotices();

}
