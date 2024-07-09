package com.edu.cqupt.shemining.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.FeatureEntity;
import com.edu.cqupt.shemining.vo.FeatureListVo;
import com.edu.cqupt.shemining.vo.FeatureVo;

import java.util.List;

// TODO 公共模块新增类
public interface FeatureManageService extends IService<FeatureEntity> {
    List<FeatureVo> getFeatureList(String belongType);

    void insertFeatures(FeatureListVo featureListVo);
    List<String> getUserFeatureList( String tablename);
}
