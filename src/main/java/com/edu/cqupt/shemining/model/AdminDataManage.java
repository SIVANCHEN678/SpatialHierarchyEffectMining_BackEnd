package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import springfox.documentation.service.ApiListing;

import java.io.Serializable;

@Data
@TableName(value ="table_describe")
public class AdminDataManage implements Serializable {
    private String id;

    private String tableId;

    private String tableName;

    private String createUser;

    private String createTime;

    private String classPath;

    private Integer uid;

    private String tableStatus;

    private float tableSize;

    //    5.6   审核下载
    private String checkApproving;
    private String checkApproved;

    private static final long serialVersionUID = 1L;
}
