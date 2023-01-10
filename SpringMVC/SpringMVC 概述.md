# SpringMVC 概述

## 什么是 SpringMVC

它是基于 MVC 开发模式的框架，用来优化控制器。它是Spring家族的一员，它也具备 IOC 和 AOP。MVC 是一种开发模式，它是模型视图控制器的简称，所有的 web 应用都是基于 MVC开发。M 指代模型层，包含实体类，业务逻辑层，数据访问层；V 是视图层， html，javaScript，Vue 等都是视图层，用来显现数据；C 是控制器，它是用来接收客户端的请求，并返回响应到客户端的组件，Servlet 就是组件。**SpringMVC 的主要作用就是优化 Servlet 的功能，包括数据提交的优化，携带数据的优化和返回处理的优化**。

SpringMVC 框架的优点包括：

* 轻量级,基于 MVC 的框架

* 易于上手，容易理解，功能强大

* 它具备 IOC 和 AOP

* 完全基于注解开发

有了 SpringMVC 的帮助，web 请求执行的流程也有所变化，所有的页面请求会首先经过由 SpringMVC 定义好的 `DispatcherServlet`，然后再交给我们自己定义的 `Controller`，`Controller` 已经是一个包含了普通方法的类而不是 Servlet 了。

&emsp;

## 一个简单的项目

首先需要在 `pom.xml` 中引入对应依赖：

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>6.0.3</version>
</dependency>
```

然后需要在 `WEB_INF` 文件夹下的 `web.xml` 下配置对应的 Servlet ，即 `DispatcherServlet`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

    <!--注册SpringMVC框架-->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springMVC.xml</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!--
          指定拦截什么样的请求
          http://localhost:8080/one
          http://localhost:8080/index.jsp
          http://localhost:8080/demo.action
          <a href="${pageContext.request.contextPath}/demo.action">访问服务器</a>
        -->
        <url-pattern>*.action</url-pattern>
    </servlet-mapping>

</web-app>
```

随后在 resources 文件夹下配置对应的 `springmvc.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--添加包扫描-->
    <context:component-scan base-package="com.example.springmvc"/>
    <!--添加视图解析器-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!--配置前缀-->
        <property name="prefix" value="/admin/"/>
        <!--配置后缀-->
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```

新建一个对应的 `Controller`：

```java
@Controller
@RequestMapping(path = "*.action")
public class DemoController {
    public DemoController(){
        System.out.println("INIT.....");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String demo(){
        System.out.println("服务器被访问到了.......");
        return "main";  //可以直接跳到/admin/main.jsp页面上
    }
}
```

当点击 `index.jsp` 上面的超链接时 `<a href="${pageContext.request.contextPath}/demo.action">REF</a>`，就会自动跳转到 `admin/main.jsp` 上了。

&emsp;

## @RequestMapping() 详解

通过 `@RequestMapping` 注解可以定义处理器对于请求的映射规则。该注解可以注解在方法上，也可以注解在类上，但意义是不同的。value 属性值常以“/”开始，`@RequestMapping` 的 value 属性用于定义所匹配请求的 URI。

一个 `@Controller` 所注解的类中，可以定义多个处理器方法。当然，不同的处理器方法所匹配的 URI 是不同的，这些不同的 URI 被指定在注解于方法之上的 `@RequestMapping` 的value 属性中。但若这些请求具有相同的 URI 部分，则这些相同的 URI部分可以被抽取到注解在类之上的 `@RequestMapping` 的 value 属性中。此时的这个 URI 表示模块（相当于包）的名称，URI 的请求是相对于 Web 的根目录。换个角度说，要访问处理器的指定方法，必须要在方法指定 URI 之前加上处理器类前定义的模块名称。例如：

```java
@RequestMapping("/user")
public class DemoAction1 {
    @RequestMapping("/1")
    public String demo1(){
        System.out.println("服务器被访问到了1.......");
    }

    @RequestMapping("/2")
    public String demo2(){
        System.out.println("服务器被访问到了2.......");
    } 
}
```

此注解也可区分 GET 请求和 POST 请求：

```java
@Controller
public class ReqAction {
    @RequestMapping(value = "/req",method = RequestMethod.GET)
	public String req(){
	    System.out.println("我是处理get请求的........");
	    return "main";
	}
	@RequestMapping(value = "/req" ,method = RequestMethod.POST)
	public String req1(){
	    System.out.println("我是处理post请求的........");
	    return "main";
	}
}
```

&emsp;

## 五种数据提交的方式

### 单个表单提交

页面：

```html
<form action="${pageContext.request.contextPath}/one.action">
     Name:<input name="name"><br>
     Age:<input name="age"><br>
     <input type="submit" value="Submit">
</form>
```

对应方法：

```java
@RequestMapping("/one")
public String one(String name, int age){  // ===> 自动注入,并且类型转换
    System.out.println("my name is "+ name + ",age = "+ String.valueOf(age));
    return "main";
}
```

请求参数可以被自动注入并且实现类型转换，需要确保表单中 name 属性对应的名字和形参一致。

&emsp;

### 对象封装提交数据

在提交请求中，保证请求参数的名称与实体类中成员变量的名称一致，则可以自动创建对象，自动提交数据，自动类型转换，自动封装数据到对象中。

```java
@RequestMapping("/two")
public String two(Users u){
    System.out.println(u);
    return "main";
}
```

**需要注意的是定义的 User 类中属性名称需要和表单中 name 属性对应的名字一样，同时需要有 `set()` 方法**。

&emsp;

### 动态占位符提交

仅限于超链接或地址拦提交数据，它是一杠一值，一杠一大括号，使用注解 `@PathVariable`来解析。

例如当前地址栏的数据是：

```html
<a href="${pageContext.request.contextPath}/three/Jack/22.action">动态提交</a>
```

```java
@RequestMapping("/three/{name}/{age}")
public String three(
        @PathVariable("name")  // ===> 用来解析路径中的请求参数
        String name,
        @PathVariable("age")
        int age){
    System.out.println("name = " + name + ", age= "+ age);
    return "main";
}
```

&emsp;

### 映射名称不一致

在前后端分离开发中，可能存在提交请求参数与 action 方法的形参名称不一致，可以使用注解 `@RequestParam` 来解析：

```java
@RequestMapping("/four")
public String four(
       @RequestParam("name")  // ===> 专门用来解决名称不一致的问题
       String uname,
       @RequestParam("age")
       int uage){
       System.out.println("uname =" + uname + ", uage=" + uage);
       return "main";
}
```

&emsp;

### 手工提取数据

```java
@RequestMapping("/five")
public String five(HttpServletRequest request){
    String name = request.getParameter("name");
    int age = Integer.parseInt(request.getParameter("age"));
    System.out.println("name=" + name + ", age=" + age);
    return "main";
}
```
