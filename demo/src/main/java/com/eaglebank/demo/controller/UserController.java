package com.eaglebank.demo.controller;

import com.eaglebank.demo.model.User;
import com.eaglebank.demo.service.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    
}
