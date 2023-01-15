package org.example.service;

import org.springframework.stereotype.Service;

// 目标类
@Service(value = "orderService")
public class OrderService {
    // 目标方法
    public void generate(){
        System.out.println("订单已生成！");
    }

    public void delete(){
        System.out.println("订单已取消！");
    }
}
