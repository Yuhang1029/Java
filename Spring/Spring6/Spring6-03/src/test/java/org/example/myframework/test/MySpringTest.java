package org.example.myframework.test;

import org.example.bean.UserService;
import org.example.myframework.ApplicationContext;
import org.example.myframework.ClassPathXmlApplicationContext;
import org.junit.Test;

public class MySpringTest {
    @Test
    public void testMySpring(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mySpring.xml");
        Object user = applicationContext.getBean("user");
        UserService userService = (UserService) applicationContext.getBean("userService");
        System.out.println(user);
        userService.save();
    }
}
