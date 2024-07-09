package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="field_management")
public class FieldManagementEntity implements Serializable {
    @TableId
    private Integer characterId;
    private String featureName;
    private String chName;
    private Boolean diseaseStandard;

    // 人口学
    private Boolean isDemography;
    // 生理指标
    private Boolean isPhysiological;

    // 行为学
    private Boolean isSociology;

    private String tableName;
    private String unit;
    private Boolean isLabel;
    private Boolean discrete;
    private String range;
    private String disease;
}
