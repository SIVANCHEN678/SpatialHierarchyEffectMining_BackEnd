package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value ="log")
public class Log {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String time;

    private String userName;

    private String ip;
}
