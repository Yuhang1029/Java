package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccountController {
    public void get(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("[AccountController] This is get() function...");
    }

    public void post(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("[AccountController] This is post() function...");
    }
}
