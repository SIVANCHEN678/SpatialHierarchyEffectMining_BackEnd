package com.edu.cqupt.shemining.model;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

// TODO 公共模块新增类

@Data
@TableName(value ="field_management")
public class FeatureEntity implements Serializable {
    @TableId
    private Integer characterId;

    private String featureName;
    private String chName;

    private boolean diseaseStandard = false;

    private boolean examine = false;

//    // 人口学
//    private Boolean diagnosis = false;
//    // 生理指标
//    private Boolean pathology = false;
//
//    // 行为学
//    private Boolean vitalSigns = false;


    private boolean clinicalRepresentation = false;

    private boolean livingHabit = false;

    private boolean socialConnection = false;

    private String tableName;
    private String unit;
    private boolean isLabel = false;

}
