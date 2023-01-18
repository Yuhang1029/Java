# Spring Boot 介绍

## 什么是 Spring Boot

Spring Boot 也是 Spring 家族中的一员，它的核心还是IOC容器。相比 Spring，SpringMVC 需要大量的配置文件，同时还需要配置各种对象，非常繁琐。SpringBoot 就相当于不需要配置文件的Spring + SpringMVC，常用的框架和第三方库都已经配置好了，拿来就可以使用了，大大提高了开发效率。

Spring Boot  的特点包括：

- 内嵌的 Tomcat， jetty ， Undertow

- 提供了 starter 起步依赖，简化应用的配置。比如使用 MyBatis 框架 ， 需要在 Spring 项目中，配置 MyBatis 的对象 SqlSessionFactory ，Dao 的代理对象；在 Spring Boot 项目中，在 pom.xml 里面, 加入一个 `mybatis-spring-boot-starter` 依赖即可。

- 自动配置，就是把 Spring 中的，第三方库中的对象都创建好，放到容器中，开发人员可以直接使用。

- 提供了健康检查， 统计，外部化配置。

- 不用生成代码， 不用使用 `.xml` 做配置。

&emsp;

## Spring 配置类

使用 Java 类作为 xml 配置文件的替代， 是配置 Spring 容器的纯 Java 的方式，可以实现全注解开发。 常用的两个注解是 `@Configuration` 和 `@Bean`：

* `@Configuration` ： 放在一个类的上面，表示这个类是作为配置文件使用的。

* `@Bean`：声明对象，把对象注入到容器中。

例如前面学过的对事务的支持：

```java
@Configuration
@ComponentScan("org.example.bank")
@EnableTransactionManagement    // 开启事务注解
public class Spring6Config {
    @Bean(name = "dataSource")
    public DataSource getDataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring6");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean(name = "dataSourceTransactionManager")
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}
```

创建一个方法，方法的返回值是对象。 在方法的上面加入 `@Bean`，方法的返回值对象就注入到容器中。当不指定对象的名称，默认是方法名是 id。

&emsp;

### 使用其他 Spring 配置文件

如果有一个已经存在的配置文件希望继续利用，可以用 `@ImportResource` 导入其他的 xml配置文件， 等于在 xml 使用下面语句：

```xml
<import resources="其他配置文件"/>
```

例如：

```java
@Configuration
@ImportResource(value ={ "classpath:applicationContext.xml","classpath:beans.xml"})
public class SpringConfig {
}
```

&emsp;

### 读取属性配置文件

`@PropertyResource` 可以读取 properties 属性配置文件。，使用属性配置文件可以实现外部化配置 ，在程序代码之外提供数据。步骤如下：

1. 在 resources 目录下，创建 properties 文件， 使用 k=v 的格式提供数据

2. 在 PropertyResource 指定 properties 文件的位置

3. 使用 @Value（value="${key}"）

```java
@Configuration
@ImportResource(value ={ "classpath:applicationContext.xml","classpath:beans.xml"})
@PropertySource(value = "classpath:config.properties")
@ComponentScan(basePackages = "com.example")
public class SpringConfig {
}
```

在配置文件中作如下配置：

```properties
tiger.name=Tom
tiger.age=10
```

在类中加上注解，即可在容器创建类的时候给属性赋值：

```java
@Component("tiger")
public class Tiger {
    @Value("${tiger.name}")
    private String name;

    @Value("${tiger.age}")
    private int age;
}
```

&emsp;

## Spring Boot 基础使用

通过 [https://start.spring.io](https://start.spring.io)，使用 Spring 提供的初始化器， 就是向导创建 Spring Boot 应用。

### 核心注解 @SpringBootApplication

在核心启动类上可以看到这个注解，它是一个复合注解，由三个核心注解构成：

* `@SpringBootConfiguration`：等同于 `@Configuration`，代表可以当作配置文件使用，可以在对应类中利用 `@Bean` 生成对象进行 Bean 管理。

* `@EnableAutoConfiguration`：启动自动配置，可以把 Java 对象配置好注入到 Spring 容器中。

* `@ComponentScan`：组件扫描器，默认扫描的包是该类所在的包和所有子包。

&emsp;

### 核心配置文件

配置文件名称是 application，这个是约定俗成的，不能更改。扩展名有两种格式， `.properties` 文件和 `.yml` 文件。下面以 yaml 文件为例：

```yml
server:
    port: 8082
    servlet:
        context-path: /myapp
```

通过配置文件可以设置端口号和上下文名称。

&emsp;

### 多环境配置

在实际开发中，会经历很多阶段，每个阶段的配置也有所不同，有开发环境， 测试环境， 上线的环境。使用多环境配置文件，可以方便的切换不同的配置。可以创建多个配置文件， 名称规则： `application-环境名称.properties/yml`

创建开发环境的配置文件： `application-dev.yml`

创建测试者使用的配置： `application-test.yml`

在 `application.yml` 中，指定需要启动的环境即可：

```yaml
spring:
  profiles:
    active: dev
```

即针对一个开发项目，最后可能会有四个配置文件，三个针对不同环境，一个负责声明运行哪一个环境的。

&emsp;

### 使用容器

可以通过 `SpringApplication.run(Application.class, args)` 的返回值获取容器。

```java
public static void main(String[] args) {
    ConfigurableApplicationContext ctx = SpringApplication.run(SpringBoot02Application.class, args);
    ctx.getBean("xxx");
}
```

其中 `ConfigurableApplicationContext` 是 `ApplicationContext` 的子接口。

&emsp;

### CommandLine 和 ApplicationRunner 接口

开发中可能存在这样的场景，需要在容器启动后来执行一些内容，比如读取配置文件，数据库连接之类的。Spring Boot 给我们提供了两个接口来帮助实现这种需求，即 `CommandLine` 和 `ApplicationRunner`，它们的执行时机都是在容器启动完成后。这两个接口中有一个 `run()` 方法，我们只需要实现这个方法即可。两个口不同之处在于入参类型不同。

```java
@SpringBootApplication
public class SpringBoot02Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot02Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ...
    }
}
```

&emsp;

## WEB 组件

### 拦截器

拦截器是 SpringMVC 中一种对象，能拦截器对 Controller 的请求。框架中有系统的拦截器， 还可以自定义拦截器，实现对请求预先处理。实现自定义拦截器需要实现`HandlerInterceptor` 接口。

之前介绍过如何在 SpringMVC 配置文件中声明拦截器，这里介绍在 Spring Boot 中如何使用：

```java
@Configuration
public class MyAppConfig implements WebMvcConfigurer {
    //添加拦截器对象， 注入到容器中
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //创建拦截器对象
        HandlerInterceptor interceptor = new LoginInterceptor();
        //指定拦截的请求uri地址
        String[] path = {"/user/**"};
        //指定不拦截的地址
        String[] excludePath = {"/user/login"};
        registry.addInterceptor(interceptor)
                .addPathPatterns(path)
                .excludePathPatterns(excludePath);
    }
}
```

定义的配置类需要实现 `WebMvcConfigurer` 接口，里面包含许多和 SpringMVC 设置有关的函数，然后只需要加上 `@Configuration` 即可完成所有配置。

&emsp;

### Servlet

自定义 Servlet 的过程和之前一样：

```java
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //使用HttpServletResponse输出数据，应答结果
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out  = resp.getWriter();
        out.println("===执行的是Servlet==");
        out.flush();
        out.close();
    }
}
```

在 Servlet 注册部分这里同样适用配置类即可：

```java
@Configuration
public class WebApplictionConfig {
    //定义方法， 注册Servlet对象
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean bean = new ServletRegistrationBean();
        bean.setServlet(new MyServlet());
        bean.addUrlMappings("/login","/test"); 
        return bean;
    }
}
```


