# 一个简单 Spring 框架的实现

Spring 技术的两大核心就是控制反转和依赖注入。在这个例子中我们通过一个简单的服务，实现手写一个读取配置文件，容器管理所有 Bean 的简单 Spring 框架。

## 项目背景

设想一个水果购物场景，我们遵循服务开发规范，设立控制层 (Controller) ，服务层 (Service) 和 数据访问层 (DAO)，针对具体的业务，由控制层调用服务层调用数据访问层实现相应的逻辑。这里为了简化，具体业务被抽象成了打印一句话，具体代码如下：

在 `controller` 包下，设计一个 `FruitController`，其中包含 `FruitService` 为一个属性。

```java
public class FruitController {
    private FruitService fruitService = new FruitServiceImpl();

    public void say() {
        System.out.println("This is fruit controller");
        fruitService.sayService();
    }
}
```

在 `service` 包下，设计一个 `FruitService` 接口和他的一个实现类 `FruitServiceImpl`。同样，实现类中 FruitDAO 是其一个属性。

```java
public interface FruitService {
    void sayService();
}


public class FruitServiceImpl implements FruitService{
    private FruitDao fruitDao = new fruitDaoImpl();

    @Override
    public void sayService() {
        System.out.println("This is fruit service");
        fruitDao.sayDAO();
    }
}
```

最后在 `dao` 包下，设计一个 `FruitDao` 和它的实现类 `FruitDaoImpl`。

```java
public interface FruitDao {
    void sayDAO();
}


public class FruitDaoImpl implements FruitDao{
    @Override
    public void sayDAO() {
        System.out.println("This is fruit dao");
    }
}
```

通过观察上述代码我们可以看出，控制层，服务层和数据控制层之间都存在耦合，需要出现例如 `FruitService fruitService = new FruitServiceImpl()` 这样子的代码，当业务场景变得更复杂，三层所需要的实现类增多之后，会造成整个服务非常复杂，需要有很繁琐的类的构造逻辑从而确保相互之间依赖关系正常。

举个例子，如果此时数据库连接部分我们希望采用新的数据库，即要重新写一个 `FruitDaoImpl2`，为了确保他可以投入使用，我们需要把之前出现过 `new FruitDaoImpl()` 的地方全部改成 `new FruitDaoImpl2()`。当程序很复杂的时候，这个改动点就会很多，从而造成所有之前运行正常的部分也需要单元测试，这样一来就违背了开闭原则 OCP。开闭原则是这样说的：在软件开发过程中应当对扩展开放，对修改关闭。也就是说，如果在进行功能扩展的时候，添加额外的类是没问题的，但因为功能扩展而修改之前运行正常的程序，这是忌讳的，不被允许的。因为一旦修改之前运行正常的程序，就会导致项目整体要进行全方位的重新测试。

同样，这种结构也违背了依赖倒置原则 (Dependence Inversion Principle)。可以很明显的看出，**上层**是依赖**下层**的。`FruitController` 依赖 `FruitServiceImpl`，而 `FruitServiceImpl` 依赖`FruitDaoImpl`，这样就会导致**下面只要改动**，**上面必然会受牵连（跟着也会改）**，所谓牵一发而动全身。

&emsp;

## 配置文件的引入

为了解决上面出现的问题，首先可以将所有的实现类写在一个 `.xml` 配置文件中。示例如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans>
    <bean id="fruitDAO" class="com.example.javaweb.dao.FruitDaoImpl"/>
    <bean id="fruitService" class="com.example.javaweb.service.FruitServiceImpl">
        <!-- property 标签用来表示属性；name 表示属性名称，在 FruitServiceImpl 定义，ref 表示引用其他 bean 的 id ，在 xml 中定义 -->
        <property name="fruitDao" ref="fruitDAO"/>
    </bean>
    <bean id="fruitController" class="com.example.javaweb.controllers.FruitController">
        <property name="fruitService" ref="fruitService"/>
    </bean>
</beans>
```

在这个配置文件中，我们把前面出现的三个类都用标签 <bean> 放入配置文件中。除了最底层的 FruitDAO，其他都需要依赖。所以新增一个 <property> 标签，标签内部的 <name> 表示在对应类中声明的属性名称，<ref> 表明他对应的 Java Bean 是哪一个，即配置文件中的 id。通过这样一个配置文件，我们详细说明了各个类之间的依赖关系。

&emsp;

## 依赖注入

首先我们设计一个 `BeanFactory` 接口，目的是在服务最顶层获得对应的 `Controller` 对象。

```java
public interface BeanFactory {
    Object getBean(String id);
}
```

然后定一个实现类 ClassPathXMLApplicationContext，继承上面的接口，而所有的核心就在这个类的构造过程如何可以实现将我们所有的与具体服务相关的类构造完成。

```java
public class ClassPathXMLApplicationContext implements BeanFactory{
    private final Map<String, Object> beanMap = new HashMap<>();

    public ClassPathXMLApplicationContext() {
        try {
            // 通过类的加载器读取 XML 配置文件
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("applicationContext.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            // 获取所有的 bean 节点
            NodeList beanList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanList.getLength(); i++) {
                Node beanNode = beanList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String id = beanElement.getAttribute("id");
                    String className = beanElement.getAttribute("class");
                    // 根据全类名获取每一个 bean 对象
                    Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
                    // 放入 beanMap 中
                    beanMap.put(id, obj);
                }
            }

            // 组装 bean 之间的依赖关系
            for (int i = 0; i < beanList.getLength(); i++) {
                Node beanNode = beanList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String id = beanElement.getAttribute("id");
                    NodeList beanChildNodes = beanElement.getChildNodes();
                    for (int j = 0; j < beanChildNodes.getLength(); j++) {
                        Node child = beanChildNodes.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("property")) {
                            Element propertyElement = (Element) child;
                            String propertyName = propertyElement.getAttribute("name");
                            String ref = propertyElement.getAttribute("ref");
                            // 找到 ref 对应的实例
                            Object refObj = beanMap.get(ref);
                            // 将 refObj 设置到当前 bean 对应的实例属性 propertyName 上
                            Object beanObj = beanMap.get(id);
                            Field propertyField = beanObj.getClass().getDeclaredField(propertyName);
                            propertyField.setAccessible(true);
                            propertyField.set(beanObj, refObj);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
```

我们定义了一个 `beanMap`，用于存放构建后的实例和他的名字，下面来详细看一下上面的代码。

首先第一部分是通过类的加载器读取对应的配置文件，需要注意配置文件的存放位置。如果是通过 Maven 构建的项目则默认存放在 `resources` 文件夹下。

随后我们查找所有的 <bean> 标签节点，因为每一个节点对应一个运行时类，当获取每一个节点时，我们提取出他的 id 名称和完整类名，通过反射构建出运行时类并且存放在 `beanMap` 中。

在完成上面的步骤后，我们已经实现了提取所有的 Java Bean，但是对于他们之间的依赖关系没有处理，所以我们需要再次利用 for 循环遍历所有的 <bean> 节点。这一次，我们需要对所有 <bean> 节点的子节点进行检查，看看哪些其中包含了 <property> 节点。当找到后，我们利用同样的方式提取出 `name` 和 `ref` 两个字段，从 `beanMap` 中找出对应依赖，同样在利用反射实现对类的属性的赋值。整个过程完成之后，所有的运行时类就构造完毕了。

针对上面三个出现的运行时类，我们也需要将所有的属性改成接口，而不能使用具体的接口实现类。

```java
public class FruitController {
    private FruitService fruitService = null;

    public void say() {
        System.out.println("This is fruit controller");
        fruitService.sayService();
    }
}
```

```java
public interface FruitService {
    void sayService();
}


public class FruitServiceImpl implements FruitService{
    private FruitDao fruitDao = null;

    @Override
    public void sayService() {
        System.out.println("This is fruit service");
        fruitDao.sayDAO();
    }
}
```

&emsp;

## 对基础类型赋值

上面的代码默认类中的属性都是其他自定义类，但是当学完 Spring 框架之后知道其实它还应该支持基本类型。下面这种方法更加完整丰富，同时利用 `dom4j` 帮助解析 xml 文件。

```java
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
```
