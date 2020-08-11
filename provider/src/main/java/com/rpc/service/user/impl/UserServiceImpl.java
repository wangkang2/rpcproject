package com.rpc.service.user.impl;

import com.rpc.annotation.RpcService;
import com.rpc.entity.User;
import com.rpc.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User getUserById(String id) {
        logger.info("入参id是{}", id);
        User user = new User();
        user.setId(id);
        user.setName("name" + id);
        return user;
    }

    @Override
    public String deleteById(String id) {
        logger.info("删除id是{}", id);
        return id;
    }
}
