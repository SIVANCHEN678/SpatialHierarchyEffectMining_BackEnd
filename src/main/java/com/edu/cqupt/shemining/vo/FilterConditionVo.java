package com.edu.cqupt.shemining.vo;


import com.edu.cqupt.shemining.model.FilterDataCol;
import com.edu.cqupt.shemining.model.FilterDataInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterConditionVo {
    private FilterDataInfo filterDataInfo;
    private List<FilterDataCol> filterDataCols;
}
