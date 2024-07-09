package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.StasticOne;

import java.util.List;


public interface StasticOneService extends IService<StasticOne> {
    StasticOne getStasticOne();

    Result getDiseases();

    List<String> getAllTableNames();

    List<String> getAllUserBuiltTableNames();



    String getType(String tablename);

    Integer getPosNumber(String tablename, String type);

    Integer getNegNumber(String tablename, String type);
}
