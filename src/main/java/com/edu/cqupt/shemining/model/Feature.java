package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value ="feature_manager")
public class Feature implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String diseaseName;

    private String featureName;

    private String featureNameCn;

    private Integer she;

    private static final long serialVersionUID = 1L;
}
