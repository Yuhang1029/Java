package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserController {
    public void get(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("[UserController] This is get() function...");
    }

    public void post(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("[UserController] This is post() function...");
    }
}
