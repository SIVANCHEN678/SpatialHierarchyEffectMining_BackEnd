package com.edu.cqupt.shemining.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO 公共模块新增类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDataFormVo {
//    新增
    private String dataType;
    private String diseaseName;
    private String userName;
    private Integer uid;
    private String isFilter;

    private String isUpload;


    private String dataName;
    private String createUser;
    private List<CreateTableFeatureVo> characterList;
    private String userList;

}
