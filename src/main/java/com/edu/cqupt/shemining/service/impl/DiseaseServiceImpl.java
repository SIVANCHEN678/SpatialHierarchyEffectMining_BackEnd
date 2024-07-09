package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.DiseaseMapper;
import com.edu.cqupt.shemining.model.Disease;
import com.edu.cqupt.shemining.service.DiseaseService;
import org.springframework.stereotype.Service;

@Service
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements DiseaseService {
}
