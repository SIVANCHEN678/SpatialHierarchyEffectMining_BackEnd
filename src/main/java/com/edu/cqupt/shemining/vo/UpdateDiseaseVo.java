package com.edu.cqupt.shemining.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiseaseVo {
    private String categoryId;
    private String diseaseName;
    private String oldName;
    private String parentDisease;
    private String parentId;
    private String icdCode;
    private String userName;
    private Integer uid;
}
