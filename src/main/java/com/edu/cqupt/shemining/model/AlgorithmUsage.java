package com.edu.cqupt.shemining.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmUsage {
    private String model;  // 算法名
    private int usageCount; // 使用次数
}
