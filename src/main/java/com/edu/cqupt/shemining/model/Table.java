package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value ="table_manager")

public class Table {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("table_name")
    private String tableName;

    private String diseaseName;

    private String description;

    private String time;

    private String tableType;

//    private Integer haveCounty;

    private static final long serialVersionUID = 1L;
}
