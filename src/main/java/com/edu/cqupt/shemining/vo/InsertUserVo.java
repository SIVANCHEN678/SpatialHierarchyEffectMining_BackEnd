package com.edu.cqupt.shemining.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertUserVo {


    private String userName;

    private String password;

    private String createTime;

    private String updateTime;

    private Integer role;

    private String userStatus;

}
