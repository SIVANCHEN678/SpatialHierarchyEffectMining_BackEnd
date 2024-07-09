package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName(value = "notification")
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @TableId(type = IdType.AUTO)
    private Integer infoId;
    private Integer uid;
    private String userName;
    private Date createTime;
    private String title;
    private String content;
    private Date updateTime;
    private String isDelete;
    private String type;
}
