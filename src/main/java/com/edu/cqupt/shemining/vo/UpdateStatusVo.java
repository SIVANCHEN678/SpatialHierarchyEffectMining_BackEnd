package com.edu.cqupt.shemining.vo;


import lombok.Data;

@Data
public class UpdateStatusVo {
    private Integer uid ;
    private Integer role;
    private  String status;
    private double uploadSize;
}
