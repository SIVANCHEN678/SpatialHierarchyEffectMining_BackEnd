package com.edu.cqupt.shemining.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feature {
    private Integer id;
    private String label;
    private Integer fill_rate;
    private Integer status;
    private Boolean showTag;
    private Boolean isLeaf;

    private String name;

    private List<Feature> children;


}