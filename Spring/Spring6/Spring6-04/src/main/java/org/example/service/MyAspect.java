package org.example.service;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

// 切面类
@Aspect
@Component
public class MyAspect {
    // 切点表达式
    @Before("execution(* org.example.service.OrderService.*(..))")
    // 这就是需要增强的代码（通知）
    public void advice(){
        System.out.println("正在生成订单...");
    }
}
