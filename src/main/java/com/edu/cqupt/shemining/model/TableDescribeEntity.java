package com.edu.cqupt.shemining.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName(value ="table_describe")
public class TableDescribeEntity {

    @TableId
    private String id;
    private String tableId;
    private String tableName;
    private String createUser;
    private String createTime;
    private String classPath;
    private Integer uid;
    private String tableStatus;
    private Double tableSize;
    private static final long serialVersionUID = 1L;
//    数据表类型
    private String dataType;
    private String diseaseName;
    private Integer region;
}
