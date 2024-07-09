package com.edu.cqupt.shemining.vo;

import lombok.Data;

import java.util.HashSet;
import java.util.List;

@Data
public class DiffReturnVo {
    private List<String> diffFirst;
    private List<String> diffSecond;
}
