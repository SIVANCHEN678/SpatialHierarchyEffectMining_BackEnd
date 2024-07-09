package com.edu.cqupt.shemining.model.QueryVo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

@TableName("user")
public class UserQueryVo {

    private String userName;

    private Integer role;

    private String userStatus;

}
