package com.edu.cqupt.shemining.common;


import lombok.Data;

@Data
public class MYRespone {

    private String respTime;
    private String respKey;
    private String serialNo;
    private String resultCode;
    private String resultMsg;
    private RespBody respBody;
}
