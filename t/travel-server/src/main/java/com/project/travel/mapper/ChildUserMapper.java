package com.project.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.User;
import org.apache.ibatis.annotations.Param;


public interface ChildUserMapper extends BaseMapper<User> {

    /**
    * 分页查询用户
    */
    Page<User> HowToGetUserPage(Page<User> page, @Param("ew") User apeUser);

}
