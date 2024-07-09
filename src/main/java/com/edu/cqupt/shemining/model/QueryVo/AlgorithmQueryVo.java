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

@TableName("algorithm")
public class AlgorithmQueryVo {

    @ApiModelProperty(value = "算法名称")
    private String algorithmName;
    @ApiModelProperty(value = "算法类型")
    private String algorithmType;
}
