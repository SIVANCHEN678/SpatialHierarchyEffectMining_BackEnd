package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

@TableName(value ="user_log")
public class UserLog {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private String userName;

    private String opTime;

    private String opType;

    private Integer role;

    public UserLog(Object o, Integer uid, Date date, String s, String userName) {
    }
}
