package com.edu.cqupt.shemining.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

@TableName(value ="mining")
public class Mining implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private String name;

    private String type;

//  增加字段
    private String  leader;
    private String  participant;
    private String  province;
    private String  city;
    private String  county;

    private String description;

    private String algorithmId;

    private String diseaseName;

    private String tableName;

    private String dataType;

    private String scoreId;

    private Integer maxDegree;

    private String faithfulnessAssumed;

    private String symmetricFirstStep;

    private String clinicalRepresentation;

    private String livingHabit;

    private String socialConnection;

    private String time;

    @TableField(exist = false)
    private Integer miningType;

    private static final long serialVersionUID = 1L;
}
