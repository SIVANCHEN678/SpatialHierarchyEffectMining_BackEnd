package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

@TableName(value ="public.user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer uid;

    private String userName;

    private String password;

    private String createTime;

    private String updateTime;

    private Integer role;

    @TableField(exist = false)
    private String token;

    // 新增字段

    @TableField(exist = false)
    private String code;

    private String userStatus;
    @TableField("answer_1")
    private String answer1;
    @TableField("answer_2")
    private String answer2;
    @TableField("answer_3")
    private String answer3;


    private double uploadSize;
}
