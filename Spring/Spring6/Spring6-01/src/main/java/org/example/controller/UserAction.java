package org.example.controller;

import org.example.service.UserService;
import org.example.service.impl.UserServiceImpl;

public class UserAction {
    private UserService userService = new UserServiceImpl();

    public void deleteRequest() {
        userService.deleteUser();;
    }
}
