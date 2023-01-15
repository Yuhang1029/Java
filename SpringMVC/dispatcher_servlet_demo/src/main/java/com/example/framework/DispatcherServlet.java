package com.example.framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
    private final Map<String,Object> beanMap = new HashMap<>();

    public DispatcherServlet() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(this.getClass().getClassLoader().getResourceAsStream("applicationContext.xml"));
        // 获取所有的 bean 标签
        List<Node> beanNodes = document.selectNodes("//bean");
        // 遍历集合
        beanNodes.forEach(beanNode -> {
            Element beanElement = (Element) beanNode;
            // 获取id
            String id = beanElement.attributeValue("id");
            // 获取 className
            String className = beanElement.attributeValue("class");
            try {
                // 通过反射机制创建对象
                Class<?> clazz = Class.forName(className);
                Object bean = clazz.getDeclaredConstructor().newInstance();
                // 存储到 Map 集合
                beanMap.put(id, bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        /**
         * 基本思路：
         *      1. 获取 Servlet 对应路径
         *      2. 根据路径找到对应的 Controller  e.g  user -> UserController
         *      3. 调用相应 Controller 的方法
         */
        String servletPath = request.getServletPath();
        servletPath = servletPath.substring(1);
        System.out.println(servletPath);
        String controllerName = servletPath + "Controller";
        Object controller = beanMap.get(controllerName);

        String operate = request.getMethod().toLowerCase();
        // 找到对应方法
        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            System.out.println(methodName);
            if (methodName.equals(operate)) {
                try {
                    m.invoke(controller, request, response);
                    return;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("No corresponding method found.");
    }
}
