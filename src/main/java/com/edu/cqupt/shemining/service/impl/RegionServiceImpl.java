package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.RegionMapper;
import com.edu.cqupt.shemining.model.Region;
import com.edu.cqupt.shemining.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<String> checkProvince() {
        return regionMapper.checkProvince();
    }

    @Override
    public List<String> checkCity() {
        return regionMapper.checkCity();
    }

    @Override
    public List<String> checkCounty() {
        return regionMapper.checkCounty();
    }
}
