package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("defaultController")
@RequestMapping("/default")
public class DefaultController {

    @GetMapping("/success")
    public ResponseEntity<?> defaultSuccess () {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
