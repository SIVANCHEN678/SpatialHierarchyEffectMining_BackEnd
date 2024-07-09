package com.edu.cqupt.shemining.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDiseaseVo{
        private String firstDisease;
        private String icdCode;
        //    private String secondDisease;
        private String parentId;
        private String userName;
        private Integer uid;
}
