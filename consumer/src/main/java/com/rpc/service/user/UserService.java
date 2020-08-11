package com.rpc.service.user;

import com.rpc.annotation.RpcInterface;
import com.rpc.entity.User;

@RpcInterface
public interface UserService {
    User getUserById(String id);
    String deleteById(String id);
}
