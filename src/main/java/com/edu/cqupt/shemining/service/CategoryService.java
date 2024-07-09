package com.edu.cqupt.shemining.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.cqupt.shemining.common.Result;
import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.vo.AddDiseaseVo;
import com.edu.cqupt.shemining.vo.UpdateDiseaseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
// TODO 公共模块新增类
public interface CategoryService extends IService<CategoryEntity> {
    List<CategoryEntity> getCategory(Integer uid);
    void removeNode(String id, String label);
    void removeNode(String id);

    void addParentDisease(String diseaseName);

    void changeStatus(CategoryEntity categoryEntity);

    List<CategoryEntity> getTaskCategory();
    List<CategoryEntity> getSpDisease();
    List<CategoryEntity> getComDisease();
    String getLabelByPid(@Param("pid") String pid);

    //    下面方法是管理员端-数据管理新增
    //    查看各等级病种
    List<CategoryEntity> getLevel2Label();
    List<CategoryEntity> getLabelsByPid(@Param("pid") String pid);

    //    新增疾病管理模块
    List<CategoryEntity> getAllDisease();
    int addCategory(AddDiseaseVo addDiseaseVo);
    Result updateCategory(UpdateDiseaseVo updateDiseaseVo);
    void removeCategorys(List<String> deleteIds);
}
