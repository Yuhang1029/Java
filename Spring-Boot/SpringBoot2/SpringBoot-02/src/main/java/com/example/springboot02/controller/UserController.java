package com.example.springboot02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class UserController {
    @RequestMapping("/user")
    public String doSome() {
        return "SpringBoot User";
    }
}
