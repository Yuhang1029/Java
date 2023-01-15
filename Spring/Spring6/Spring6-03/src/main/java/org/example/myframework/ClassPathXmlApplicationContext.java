package org.example.myframework;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Method;
import java.util.*;

public class ClassPathXmlApplicationContext implements ApplicationContext{
    /**
     * 存储 bean 的 Map 集合
     */
    private final Map<String,Object> beanMap = new HashMap<>();

    /**
     * 在该构造方法中，解析 mySpring.xml 文件，创建所有的 Bean 实例，并将 Bean 实例存放到Map集合中。
     * @param resource 配置文件路径（要求在类路径当中）
     */
    public ClassPathXmlApplicationContext(String resource) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(this.getClass().getClassLoader().getResourceAsStream(resource));
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
            // 再重新遍历集合，这次遍历是为了给 Bean 的所有属性赋值。
            // 思考：为什么不在上面的循环中给 Bean 的属性赋值，而在这里再重新遍历一次呢？
            // 通过这里你是否能够想到 Spring 是如何解决循环依赖的：实例化和属性赋值分开。
            beanNodes.forEach(beanNode -> {
                Element beanElt = (Element) beanNode;
                // 获取 bean 的 id
                String beanId = beanElt.attributeValue("id");
                // 获取所有 property 标签
                List<Element> propertyElements = beanElt.elements("property");
                // 遍历所有属性
                propertyElements.forEach(propertyElt -> {
                    try {
                        // 获取属性名
                        String propertyName = propertyElt.attributeValue("name");
                        // 获取属性类型
                        Class<?> propertyType = beanMap.get(beanId).getClass().getDeclaredField(propertyName).getType();
                        // 获取 set 方法名
                        String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                        // 获取 set 方法
                        Method setMethod = beanMap.get(beanId).getClass().getDeclaredMethod(setMethodName, propertyType);
                        // 获取属性的值，值可能是 value，也可能是 ref。
                        // 获取 value
                        String propertyValue = propertyElt.attributeValue("value");
                        // 获取 ref
                        String propertyRef = propertyElt.attributeValue("ref");
                        if (propertyValue != null) {
                            // 该属性是简单属性
                            Object propertyVal = null;
                            // 获取简单类型的名字 e.g. int, float
                            String propertyTypeSimpleName = propertyType.getSimpleName();
                            switch (propertyTypeSimpleName) {
                                case "byte", "Byte" -> propertyVal = Byte.valueOf(propertyValue);
                                case "short", "Short" -> propertyVal = Short.valueOf(propertyValue);
                                case "int", "Integer" -> propertyVal = Integer.valueOf(propertyValue);
                                case "long", "Long" -> propertyVal = Long.valueOf(propertyValue);
                                case "float", "Float" -> propertyVal = Float.valueOf(propertyValue);
                                case "double", "Double" -> propertyVal = Double.valueOf(propertyValue);
                                case "boolean", "Boolean" -> propertyVal = Boolean.valueOf(propertyValue);
                                case "char", "Character" -> propertyVal = propertyValue.charAt(0);
                                case "String" -> propertyVal = propertyValue;
                            }
                            setMethod.invoke(beanMap.get(beanId), propertyVal);
                        }
                        if (propertyRef != null) {
                            // 该属性不是简单属性
                            setMethod.invoke(beanMap.get(beanId), beanMap.get(propertyRef));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String beanId) {
        return beanMap.get(beanId);
    }
}
