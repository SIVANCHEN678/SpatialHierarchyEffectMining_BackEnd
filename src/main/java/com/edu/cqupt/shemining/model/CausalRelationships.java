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
@TableName(value ="causal_relationships")
public class CausalRelationships {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String relationship;

    private String diseaseName;

    private String tableName;

    private String algorithmName;

    private String priorKnowledgeName;

    private String time;


    private static final long serialVersionUID = 1L;
}
