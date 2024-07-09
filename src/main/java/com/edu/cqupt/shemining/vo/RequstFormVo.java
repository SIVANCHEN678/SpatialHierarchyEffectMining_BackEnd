package com.edu.cqupt.shemining.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequstFormVo {
    private String stringFactor;
    private String diseaseName;
    private List<String> listStr;
    private Integer uid;

}
