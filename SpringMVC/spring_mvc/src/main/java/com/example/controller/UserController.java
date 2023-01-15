package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userController")
@RequestMapping("/myapp")
public class UserController {
    @Resource(name = "userService")
    private UserService userService;

    public UserController() {
        System.out.println("UserController init...");
    }

    @GetMapping(value = "/users")
    public ResponseEntity<?> getAllUsers () {
        System.out.println("getAllUsers()...");
        List<User> ans = userService.getAllUsers();
        System.out.println(ans);
        return new ResponseEntity<>(ans, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<?> getUserById (@PathVariable int userId) {
        System.out.println("getUserById()...");
        User ans = userService.getUserById(userId);
        return new ResponseEntity<>(ans, HttpStatus.OK);
    }
}
