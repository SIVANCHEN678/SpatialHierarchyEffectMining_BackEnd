package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value ="log")
@Data
public class LogEntity {
    @TableId
    private Integer id;

    private Integer uid;
    private String userName;
    private String operation;
    private String opTime;
    private Integer role;

    private static final long serialVersionUID = 1L;
}

