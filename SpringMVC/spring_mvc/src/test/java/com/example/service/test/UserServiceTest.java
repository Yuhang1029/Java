package com.example.service.test;

import com.example.model.User;
import com.example.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class UserServiceTest {
    @Test
    public void testUserService() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springMVC.xml");
        UserService userService = applicationContext.getBean("userService", UserService.class);
        List<User> list = userService.getAllUsers();
        System.out.println(list);
    }
}
