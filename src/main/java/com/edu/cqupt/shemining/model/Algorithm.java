package com.edu.cqupt.shemining.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value ="algorithm")
public class Algorithm {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String algorithmName;
    private  String scoreId;

    private String maxDegree;

    private String algorithmType;

    private String algorithmDescription;

    private String time;

    private String dataType;

    private static final long serialVersionUID = 1L;
}
