package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.MiningMapper;
import com.edu.cqupt.shemining.model.Mining;
import com.edu.cqupt.shemining.service.MiningService;
import org.springframework.stereotype.Service;

@Service
public class MiningServiceImpl extends ServiceImpl<MiningMapper, Mining> implements MiningService {
}
