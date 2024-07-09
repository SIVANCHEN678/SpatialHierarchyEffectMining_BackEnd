package com.edu.cqupt.shemining.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.cqupt.shemining.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Integer updateByname(String newpassword,String username);

    @Update("UPDATE public.user SET upload_size = upload_size-#{size} WHERE uid = #{id}")
    int decUpdateUserColumnById(@Param("id") Integer id, @Param("size") Double size);

    @Update("UPDATE public.user SET upload_size = upload_size+#{size} WHERE uid = #{id}")
    int recoveryUpdateUserColumnById(@Param("id") Integer id, @Param("size") Double size);

    User queryByUername(String username);

    /**
     * 下面方法是管理员端-数据管理新增
     */

    User selectByUid(Integer uid);
    void addTableSize(Integer uid, double tableSize);
    void minusTableSize(Integer uid, double tableSize);
}
