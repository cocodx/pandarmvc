package com.javalol.service.service.impl;

import com.javalol.mvc.annotation.Service;
import com.javalol.service.service.UserService;

@Service(value="userService")
public class UserServiceImpl implements UserService {
    @Override
    public String getUser() {
        System.out.println("获取用户信息");
        return "forward:/success.jsp";
    }
}
