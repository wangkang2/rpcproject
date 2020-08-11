package com.rpc.service.user;

import com.rpc.entity.User;

public interface UserService {
    User getUserById(String id);
    String deleteById(String id);
}
