package com.edu.cqupt.shemining.service;

import com.edu.cqupt.shemining.model.CategoryEntity;
import com.edu.cqupt.shemining.vo.CreateTableFeatureVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类
public interface TableDataService {
    List<LinkedHashMap<String,Object>> getTableData(String TableId, String tableName);

    List<String> uploadFile(MultipartFile file, String tableName, String type, String user, Integer uid, String parentId, String parentType,String status,Double size,String is_upload,String is_filter,
                            String dataTpye, Integer region) throws IOException, ParseException;

    void createTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, CategoryEntity nodeData, Integer uid,
                     String username, String IsFilter, String IsUpload,
                     String diseaseName);

    List<LinkedHashMap<String, Object>> getFilterDataByConditions(List<CreateTableFeatureVo> characterList,CategoryEntity nodeData);

    List<Map<String, Object>> getInfoByTableName(String tableName);

    List<String> ParseFileCol(MultipartFile file, String tableName) throws IOException;

    Integer getCountByTableName(String tableName);

    /**
     * 新增纳排
     */
    void createFilterBtnTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser,String status,Integer uid,String username,
                              String IsFilter,String IsUpload,String uid_list,String nodeid, String diseaseName);


    List<LinkedHashMap<String, Object>> getFilterDataByConditionsByDieaseId(List<CreateTableFeatureVo> characterList,Integer uid,String username,String nodeid);

}
