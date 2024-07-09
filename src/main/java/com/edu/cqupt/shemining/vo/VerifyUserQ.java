package com.edu.cqupt.shemining.vo;

import lombok.Data;

@Data
public class VerifyUserQ {
    private String userName;
    private String q1; // 问题1   拼接答案
    private String q2;
    private String q3;
}
