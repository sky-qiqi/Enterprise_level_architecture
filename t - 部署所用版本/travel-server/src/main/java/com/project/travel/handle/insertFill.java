package com.project.travel.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.project.travel.domain.User;
import com.project.travel.service.UserService;
import com.project.travel.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class insertFill implements MetaObjectHandler {

    @Autowired
    private UserService userService;

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        String id = TokenUtils.getUserIdByToken();
        User user = userService.getById(id);
        metaObject.setValue("createTime", new Date());
        metaObject.setValue("updateTime", new Date());
        if (user != null) {
            metaObject.setValue("createBy", user.getUserName());
            metaObject.setValue("updateBy", user.getUserName());
        }
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        String id = TokenUtils.getUserIdByToken();
        User user = userService.getById(id);
        metaObject.setValue("updateTime", new Date());
        if (user != null) {
            metaObject.setValue("updateBy", user.getUserName());
        }
    }

}
