# SpringMVC 概述

**参考文档**：[SpringMVC 完全注解方式配置](https://blog.csdn.net/qq_41865229/article/details/121588209?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-121588209-blog-70666623.pc_relevant_aa&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-121588209-blog-70666623.pc_relevant_aa&utm_relevant_index=2)

---

## 什么是 SpringMVC

它是基于 MVC 开发模式的框架，用来优化控制器。它是 Spring 家族的一员，它也具备 IOC 和 AOP。MVC 是一种开发模式，它是模型视图控制器的简称，所有的 web 应用都是基于 MVC开发。M 指代模型层，包含实体类，业务逻辑层，数据访问层；V 是视图层， html，javaScript，Vue 等都是视图层，用来显现数据；C 是控制器，它是用来接收客户端的请求，并返回响应到客户端的组件，Servlet 就是组件。**SpringMVC 的主要作用就是优化 Servlet 的功能，包括数据提交的优化，携带数据的优化和返回处理的优化**。

SpringMVC 框架的优点包括：

* 轻量级,基于 MVC 的框架

* 易于上手，容易理解，功能强大

* 它具备 IOC 和 AOP

* 完全基于注解开发

有了 SpringMVC 的帮助，web 请求执行的流程也有所变化，所有的页面请求会首先经过由 SpringMVC 定义好的 `DispatcherServlet`，然后再交给我们自己定义的 `Controller`，`Controller` 已经是一个包含了普通方法的类而不是 Servlet 了。

&emsp;

## SpringMVC 常用组件

* **DispatcherServlet**：前端控制器，由框架提供，负责统一处理请求和响应，是整个流程控制的中心，由它调用其他组件处理用户请求。

* **HandlerMapping**：处理器映射器，由框架提供，根据请求的 URL，请求方法等信息查找 Handler，即控制器方法。

* **Handler / Controller**：处理器，由程序员根据业务需求开发。

* **HandlerAdapter**：处理器适配器，由框架提供，负责对相应的处理器方法调用。

* **ViewResolver**：视图解析器，由框架提供，进行视图解析，得到相对应视图。

&emsp;

## SpringMVC 执行流程

1. 用户向浏览器发送请求，请求被 SpringMVC 前端控制器 `DispatcherServlet` 捕获。

2. `DispatcherServlet` 进行 URL 的解析，得到请求资源标识符 (URI)，判断其对应的映射：
   
   * 如果不存在，判断是否配置了 `mvc:default-servlet-handler`。
     
     * 如果没有配置，则控制台报映射找不到错误，浏览器显示 404 错误。
     
     * 如果配置了，则访问目标资源，一般为静态资源，找不到也会显示 404 错误。
   
   * 如果存在，根据该 URI，调用 `HandlerMapping` 获得该 Handler 配置的所有相关对象 （控制器和拦截器），最后以 HandlerExecutionChain 执行链对象的形式返回。

3. `DispatcherServlet` 根据获得的 Handler，选择一个 `HandlerAdapter`，提取 Request 中的模型数据，填充参数，开始执行。这里面就包括了一些额外工作，例如 `HttpMessageConverter`，数据转换，格式化等等。

4. Handler 执行完成后，向 `DispatcherServlet` 返回一个 `ModelAndView` 对象。

5. 渲染视图，将结果返回给客户端。

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

此注解也可区分 GET 请求和 POST 请求，如果不指明则默认支持所有类型请求：

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

此外，SpringMVC 还提供了派生注解，这样就可以不需要特意指明 `method`：

```java
@GetMapping     // 处理 GET 请求
@PostMapping    // 处理 POST 请求
@PutMapping     // 处理 PUT 请求
@DeleteMapping  // 处理 DELETE 请求
```

&emsp;

## 五种获取请求参数的方式

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

仅限于超链接或地址拦提交数据，它是一杠一值，一杠一大括号，使用注解 `@PathVariable`来解析，适合 RestFul 风格。

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

除了 value 这个属性，还可以设置该参数是否是必须的，以及默认值：

```java
public String four(
       @RequestParam(value = "name", required = false, defaultValue = "Tom")
       String uname,
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

&emsp;

## HttpMessageConverter

`HttpMessageConverter`  是报文信息转换器，可以将请求报文转换成 Java 对象，或者将 Java 对象。我们知道，HTTP 请求响应报文其实都是字符串，当请求报文到 Java 程序会被封装为一个 `ServletInputStream` 流，开发人员再读取报文，响应报文则通过 `ServletOutputStream` 流，来输出响应报文。从流中只能读取到原始的字符串报文，同样输出流也是。那么在报文到达 SpringMVC / SpringBoot 和从 SpringMVC / SpringBoot 出去，都存在一个字符串到 Java 对象的转化问题。这一过程，在 SpringMVC / SpringBoot中，是通过 `HttpMessageConverter` 来解决的。

![](https://upload-images.jianshu.io/upload_images/748537-a5d2807ebd2d7df1.png?imageMogr2/auto-orient/strip|imageView2/2/w/683/format/webp)

&emsp;

### @RequestBody

可以获取请求体，需要在控制器方法设置一个形参，使用 `@RequestBody` 进行标识，当前请求的请求体就会为当前注解所标识的形参赋值。

```java
@RequestMapping("/test01")
@public String testRequestBody(@RequestBody String requestBody) {
    System.out.println(requestBody);
    return "";
}
```

&emsp;

### @RequestEntity

用来封装请求报文，需要在控制器方法设置一个形参，使用 `@RequestEntity` 进行标识，当前请求的请求报文就会为当前注解所标识的形参赋值。

```java
@RequestMapping("/test02")
public String testRequestEntity(@RequestEntity String requestEntity) {
    System.out.println(requestEntity.getHeaders());
    System.out.println(requestEntity.getBody());
    return "";
}
```

&emsp;

### @ResponseBody

用于标识一个控制器方法，可以将该方法的返回值直接作为响应报文的响应体响应到浏览器。

```java
@RequestMapping("/test03")
@ResponseBody
public String testResponseBody() {
    return "success";
}
```

这时候页面上会显示 success。

这里需要注意，目前为止所有的返回都是以字符串的形式呈现，如果希望返回一个查询到的类应该怎么办呢？肯定不能直接返回一个类，因为浏览器并不知道它是什么，这个时候就需要使用 JSON。

首先需要在 maven 中导入 jackson 的依赖：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.14.0</version>
</dependency>
```

然后需要在 SpringMVC.xml 的配置文件中开启 mvc 注解驱动，此时在 HandlerAdaptor 中会自动装配一个消息转换器，可以将响应到浏览器的 Java 对象转换为 Json 格式的字符串。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven/>
</beans>
```

此时把 Java 对象作为控制器方法的返回值返回时，就会自动转换为 Json 格式字符串。

```java
@RestController
@RequestMapping("/user")
public class UserControler {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/{userId}")
    public User getUserById (@PathVariable int userId)
        return userService.findUserById(userId);
    }
}
```

&emsp;

### @RestController

这是 SpringMVC 提供的一个复合注解，标识在控制器的类上，相当于为类添加了 `@Controller` 注解，并且在其中每一个方法上都添加了 `@ResponseBody` 注解。

&emsp;

### ResponseEntity

控制器方法的返回类型，返回值就是响应到浏览器的响应报文。

```java
@RestController
@RequestMapping("/users")
public class UserControler {
    @Autowired
    private UserService userService ;
    /**
     * 查询所有
     * @return
     */
    @GetMapping(produces = "application/json;charset=utf-8")
    public ResponseEntity<List<User>> findAll(){
        List<User> users = userService.findAll();
        return new ResponseEntity<List<User>>(users , HttpStatus.OK);
    }
}
```

&emsp;

## 全注解开发

使用配置类和注解来代替 web.xml 和 SpringMVC 的配置文件，上方有参考文档可供参考。

### 创建初始化类

创建初始化类的目的是代替 `web.xml`。在 Servlet 3.0 环境中，容器会在类路径中查找实现 `ServletContainerInitializer` 接口的类，如果找到了就用它来配置 Servlet 容器。在 Spring 中，可以通过一个自定义类来继承 `AbstractAnnotationConfigDispatcherServletInitializer` ，用它来配置 Servlet 上下文。

```java
// 代替 web.xml，即 WEB 工程的初始化类
public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    // 指定 Spring 的配置类
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    // 指定 SpringMVC 的配置类
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMVCWebConfig.class};
    }

    // 指定 DispatcherServlet 的映射规则，即 url-pattern
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // 注册过滤器
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceResponseEncoding(true);

        return new Filter[]{characterEncodingFilter};
    }
}
```

&emsp;

### 创建 Spring 配置类

针对 SpringConfig 类，可以配置和数据库连接有关的配置，这一部分在 Spring 里有涉及：

```java
@Configuration
public class SpringConfig {
    @Bean(name = "dataSource")
    public DataSource getDataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/bank");
        dataSource.setUsername("root");
        dataSource.setPassword("mysql123!");
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }
}
```

&emsp;

### 创建 SpringMVC 配置类

针对 SpringMVCWebConfig 类，它是用来设置和 SpringMVC 相关的配置，包括注解驱动，拦截器，文件上传解析器等。

```java
@Configuration
@ComponentScan("com.example")
@EnableWebMvc    // 开启 MVC 注解驱动，等价于 <mvc:annotation-driven/>
public class SpringMVCWebConfig implements WebMvcConfigurer {
    @Bean
    public GeneralInterceptor generalInterceptor() {
        return new GeneralInterceptor();
    }

    // 利用默认的 Servlet 处理静态资源
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
       configurer.enable();
    }

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(generalInterceptor()).addPathPatterns("/myapp/**");
    }
}
```
