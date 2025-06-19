package com.project.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.travel.domain.User;

public interface UserService extends IService<User> {

    Page<User> getUserPage(User apeUser);

}
