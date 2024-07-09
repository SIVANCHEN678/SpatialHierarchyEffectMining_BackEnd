package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.Region;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper
public interface RegionMapper extends BaseMapper<Region> {

    List<String> checkProvince();
    List<String> checkCity();
    List<String> checkCounty();
}
