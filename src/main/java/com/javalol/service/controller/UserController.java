package com.javalol.service.controller;

import com.javalol.mvc.annotation.*;
import com.javalol.service.entity.User;
import com.javalol.service.service.UserService;

@Controller
public class UserController {

    @Autowired(value = "userService")
    private UserService userService;

    @RequestMapping("/findUser")
    public String testMethod(@RequestParam(value = "name",required = false)String name){
        System.out.println("获取参数name："+name);
        return userService.getUser();
    }

    @RequestMapping("/getData")
    @ResponseBody //返回json格式数据
    public User testMethod1(){
        User user = new User();
        user.setUserName("root");
        user.setPassword("password");
        return user;
    }

    //注入参数和注入json格式的参数，jdk8能够拿到插件。
}
