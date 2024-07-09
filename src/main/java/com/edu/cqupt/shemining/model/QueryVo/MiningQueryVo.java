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

@TableName("mining")
public class MiningQueryVo {

    @ApiModelProperty(value = "疾病名称")
    private String diseaseName;
    @ApiModelProperty(value = "算法名称")
    private String algorithmId;
    @ApiModelProperty(value = "任务类型")
    public String type;
    @ApiModelProperty(value = "数据表名")
    public String tableName;
}
