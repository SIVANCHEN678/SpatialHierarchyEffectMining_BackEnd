package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.FeatureMapper;
import com.edu.cqupt.shemining.model.Feature;
import com.edu.cqupt.shemining.service.FeatureService;
import org.springframework.stereotype.Service;

@Service
public class FeatureServiceImpl extends ServiceImpl<FeatureMapper, Feature> implements FeatureService {
}
