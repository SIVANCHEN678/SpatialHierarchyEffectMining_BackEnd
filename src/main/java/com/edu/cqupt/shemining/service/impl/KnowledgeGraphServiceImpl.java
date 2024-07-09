package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.KnowledgeGraphMapper;
import com.edu.cqupt.shemining.model.KnowledgeGraph;
import com.edu.cqupt.shemining.service.KnowledgeGraphService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeGraphServiceImpl extends ServiceImpl<KnowledgeGraphMapper, KnowledgeGraph> implements KnowledgeGraphService {
}
