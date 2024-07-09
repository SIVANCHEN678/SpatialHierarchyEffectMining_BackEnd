package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@TableName(value ="mining")
public class StasticOne {
    // 统计信息1
    private Integer specialDiseaseNumber;
    private Integer  itemNumber;
    private String startTime;
    private String endTime;
    private Integer  taskNumber;

}
