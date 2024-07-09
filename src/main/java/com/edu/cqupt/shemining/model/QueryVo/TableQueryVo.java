package com.edu.cqupt.shemining.model.QueryVo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

@TableName("table_manager")
public class TableQueryVo {

    @ApiModelProperty(value = "病种")
    private String diseaseName;

    @ApiModelProperty(value = "空间层次")
    private String tableType;

}
