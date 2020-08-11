package com.rpc.controller;

import com.rpc.entity.User;
import com.rpc.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    private TestService testService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "getUserById/{id}")
    public User getUserById(@PathVariable(value = "id") String id){
        return userService.getUserById(id);
    }

//    @GetMapping(value = "test")
//    public void getUserById(){
//        testService.exe();
//    }
}
