package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.model.Region;

import java.util.List;

public interface RegionService extends IService<Region> {
    List<String> checkProvince();
    List<String> checkCity();
    List<String> checkCounty();
}
