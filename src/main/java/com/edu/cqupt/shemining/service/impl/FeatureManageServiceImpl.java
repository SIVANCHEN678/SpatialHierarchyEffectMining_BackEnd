package com.edu.cqupt.shemining.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.cqupt.shemining.mapper.FeatureManageMapper;
import com.edu.cqupt.shemining.model.FeatureEntity;
import com.edu.cqupt.shemining.service.FeatureManageService;
import com.edu.cqupt.shemining.vo.FeatureClassVo;
import com.edu.cqupt.shemining.vo.FeatureListVo;
import com.edu.cqupt.shemining.vo.FeatureVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO 公共模块新增类

@Service
public class FeatureManageServiceImpl extends ServiceImpl<FeatureManageMapper, FeatureEntity> implements FeatureManageService {
    @Autowired
    FeatureManageMapper featureManageMapper;
    @Override
    public List<FeatureVo> getFeatureList(String belongType) {
        List<FeatureEntity> featureEntities = featureManageMapper.selectFeatures(belongType);
        //封装vo
        ArrayList<FeatureVo> featureVos = new ArrayList<>();
        for (FeatureEntity featureEntity : featureEntities) {
            FeatureVo featurevVo = new FeatureVo();
            featurevVo.setCharacterId(featureEntity.getCharacterId());
            featurevVo.setChName(featureEntity.getChName());
            featurevVo.setFeatureName(featureEntity.getFeatureName());
            featurevVo.setUnit(featureEntity.getUnit());
            //TODO 封装字段是否离散 离散就是discrete  连续就是continuous

            //TODO 如果离散就封装取值 连续就是封装范围
            featureVos.add(featurevVo);
        }
        return featureVos;
    }

    @Override
    public void insertFeatures(FeatureListVo featureListVo) {
        // 封装对象信息
        List<FeatureClassVo> tableHeaders = featureListVo.getTableHeaders();
        ArrayList<FeatureEntity> featureEntities = new ArrayList<>();
        for (FeatureClassVo tableHeader : tableHeaders) {
            FeatureEntity featureEntity = new FeatureEntity();
            featureEntity.setChName(null);// TODO 特征中文解释
            featureEntity.setFeatureName(tableHeader.getFieldName());
            featureEntity.setUnit("character varying"); // TODO  特征类型
            featureEntity.setDiseaseStandard(false);
            if(tableHeader.getIsDiagnosis()=="1") featureEntity.setClinicalRepresentation(true);
            if(tableHeader.getIsPathology()=="1") featureEntity.setLivingHabit(true);
            if(tableHeader.getIsExamine()=="1") featureEntity.setExamine(true);
            if(tableHeader.getIsLabel()=="1") featureEntity.setLabel(true);
            if(tableHeader.getIsVitalSigns()=="1") featureEntity.setSocialConnection(true);
            featureEntities.add(featureEntity);
        }
        // TODO 作废方法，不需要完成
    }
    @Override
    public List<String> getUserFeatureList(String tablename) {
        return featureManageMapper.getUserFeatureList(tablename);
    }
}
