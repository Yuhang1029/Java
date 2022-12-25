# Spring 概述

## 控制反转

控制反转 (Inversion of Control，缩写为IoC)，是面向对象编程中的一种设计思想，可以用来降低代码之间的耦合度，符合依赖倒置原则。控制反转的核心是：**将对象的创建权交出去，将对象和对象之间关系的管理权交出去，由第三方容器来负责创建与维护**。具体反转的就是两件事情，第一是程序员不再采用硬编码的方式来 new 对象，这个权利交出去；第二是程序员不再采用硬编码的方式来维护对象之间的关系，这个权利也交出去。控制反转常见的实现方式：依赖注入（Dependency Injection，简称DI）。通常，依赖注入的实现由包括两种方式：

- set方法注入

- 构造方法注入

而 Spring 框架就是一个实现了 IoC 思想的框架。

&emsp;

## Spring 八大模块

![image.png](https://cdn.nlark.com/yuque/0/2022/png/21376908/1663726169861-b5acb757-17e0-4d3d-a811-400eb7edd1b3.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_29%2Ctext_5Yqo5Yqb6IqC54K5%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10)

1. Spring Core模块
   
   这是 Spring 框架最基础的部分，它提供了依赖注入（DependencyInjection）特征来实现容器对 Bean 的管理。核心容器的主要组件是 BeanFactory，BeanFactory 是工厂模式的一个实现，是任何 Spring 应用的核心。它使用 IoC 将应用配置和依赖从实际的应用代码中分离出来。

2. Spring Context 模块
   
   如果说核心模块中的 BeanFactory 使 Spring 成为容器的话，那么上下文模块就 Spring 成为框架的原因。这个模块扩展了 BeanFactory，增加了对国际化（I18N）消息、事件传播、验证的支持。另外提供了许多企业服务，例如电子邮件、JNDI 访问、EJB 集成、远程以及时序调度（scheduling）服务。也包括了对模版框架例如 Velocity 和 FreeMarker 集成的支持。

3. Spring AOP 模块
   
   Spring 在它的 AOP 模块中提供了对面向切面编程的丰富支持，Spring AOP 模块为基于 Spring 的应用程序中的对象提供了事务管理服务。通过使用 Spring AOP，不用依赖组件，就可以将声明性事务管理集成到应用程序中，可以自定义拦截器、切点、日志等操作。

4. Spring DAO模块
   
   提供了一个 JDBC 的抽象层和异常层次结构，消除了烦琐的JDBC编码和数据库厂商特有的错误代码解析，用于简化 JDBC。

5. Spring ORM 模块
   
   Spring 提供了ORM 模块。Spring 并不试图实现它自己的 ORM 解决方案，而是为几种流行的 ORM 框架提供了集成方案，包括 Hibernate、JDO 和 iBATIS SQL 映射，这些都遵从 Spring 的通用事务和 DAO 异常层次结构。

6. Spring Web MVC模块
   
   Spring 为构建 Web 应用提供了一个功能全面的 MVC 框架。虽然 Spring 可以很容易地与其它 MVC 框架集成，例如 Struts，但 Spring 的 MVC 框架使用 IoC 对控制逻辑和业务对象提供了完全的分离。

7. Spring WebFlux 模块
   
   Spring Framework 中包含的原始 Web 框架 Spring Web MVC 是专门为 Servlet API 和 Servlet 容器构建的。反应式堆栈 Web 框架 Spring WebFlux 是在 5.0 版的后期添加的。它是完全非阻塞的，支持反应式流(Reactive Stream)背压，并在 Netty，Undertow 和Servlet 3.1+容器等服务器上运行。

8. Spring Web 模块
   
   Web 上下文模块建立在应用程序上下文模块之上，为基于 Web 的应用程序提供了上下文，提供了 Spring 和其它 Web 框架的集成，比如 Struts、WebWork。还提供了一些面向服务支持，例如：实现文件上传的 multipart 请求。

    

&emsp;

## Spring 框架特点

1. 轻量
   
   从大小与开销两方面而言 Spring 都是轻量的。完整的 Spring 框架可以在一个大小只有1MB 多的 JAR 文件里发布，并且 Spring 所需的处理开销也是微不足道的。同时，Spring 是非侵入式的，Spring 应用中的对象不依赖于 Spring 的特定类。

2. 控制反转
   
   Spring 通过一种称作控制反转（IoC）的技术促进了松耦合。当应用了IoC，一个对象依赖的其它对象会通过被动的方式传递进来，而不是这个对象自己创建或者查找依赖对象。你可以认为 IoC 与 JNDI 相反——不是对象从容器中查找依赖，而是容器在对象初始化时不等对象请求就主动将依赖传递给它。

3. 面向切面
   
   Spring 提供了面向切面编程的丰富支持，允许通过分离应用的业务逻辑与系统级服务（例如审计（auditing）和事务（transaction）管理）进行内聚性的开发。应用对象只实现它们应该做的——完成业务逻辑——仅此而已。它们并不负责（甚至是意识）其它的系统级关注点，例如日志或事务支持。

4. 容器
   
   Spring 包含并管理应用对象的配置和生命周期，在这个意义上它是一种容器，你可以配置你的每个 bean 如何被创建——基于一个可配置原型（prototype），你的 bean 可以创建一个单独的实例或者每次需要时都生成一个新的实例——以及它们是如何相互关联的。然而，Spring不应该被混同于传统的重量级的EJB容器，它们经常是庞大与笨重的，难以使用。

5. 框架
   
   Spring 可以将简单的组件配置、组合成为复杂的应用。在 Spring 中，应用对象被声明式地组合，典型地是在一个 XML 文件里。Spring 也提供了很多基础功能（事务管理、持久化框架集成等等），将应用逻辑的开发留给了你。

所有 Spring 的这些特征使你能够编写更干净、更可管理、并且更易于测试的代码。它们也为Spring 中的各种模块提供了基础支持。

&emsp;

## 一个简单的 Spring 程序

首先构造一个类 User，然后在 test 文件中进行测试：

```java
public class User {
}

public class Test {
    @Test
    public void test(){
        // 初始化Spring容器上下文（解析 beans.xml 文件，创建所有的 bean 对象）
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        // 根据 id 获取 bean 对象
        Object userBean = applicationContext.getBean("user");
        System.out.println(userBean);
    }
}
```

Bean 可以通过 `.xml` 文件进行配置。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userBean" class="org.example.bean.User"/>
</beans>
```

上述代码非常简单，但是有几个小的细节需要注意：

* <bean> 标签中的 id 属性代表对象的**唯一标识**，不能重复，class 属性用来指定要创建的java 对象的类名，这个类名必须是全限定类名（带包名）。

* Spring 是通过调用类的无参数构造方法来创建对象的，所以要想让 Spring 给你创建对象，必须保证无参数构造方法是存在的。

* Spring 配置文件中配置的 bean 可以任意类，包括 JDK 中定义的，只要这个类不是抽象的，并且提供了无参数构造方法。

* `getBean()` 方法调用时，如果指定的 id 不存在会怎样会报错。
