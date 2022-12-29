# Spring 对 IoC 的实现

## IoC 控制反转

控制反转是一种思想，控制反转是为了降低程序耦合度，提高程序扩展力，达到 OCP 原则，达到DIP原则。在前面说过，控制反转反转的主要是两个部分：

- 将对象的创建权利交出去，交给第三方容器负责。

- 将对象和对象之间关系的维护权交出去，交给第三方容器负责。

控制反转这种思想的实现靠的是依赖注入（Dependency Injection）。

&emsp;

## 依赖注入

依赖注入实现了控制反转的思想。Spring 通过依赖注入的方式来完成 Bean 管理的，这里Bean 管理说的是 Bean 对象的创建，以及 Bean 对象中属性的赋值（或者叫做 Bean 对象之间关系的维护）。依赖注入拆分来理解，依赖指的是对象和对象之间的关联关系。注入指的是一种数据传递行为，通过注入行为来让对象和对象产生关系。

依赖注入常见的实现方式包括两种：

- 第一种：set 注入
- 第二种：构造注入

&emsp;

### set 注入

```java
public class UserService {
    private UserDao userDao;

    // 使用 set 方式注入，必须提供 set 方法。
    // 反射机制要调用这个方法给属性赋值的。
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void save(){
        userDao.insert();
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userDaoBean" class="com.powernode.spring6.dao.UserDao"/>
    <bean id="userServiceBean" class="com.powernode.spring6.service.UserService">
        <property name="userDao" ref="userDaoBean"/>
    </bean>

</beans>
```

这里是通过 <property> 标签获取到属性名 `userDao` ，然后通过属性名推断出 set 方法名 `setUserDao`，再通过反射机制调用 `setUserDao()` 方法给属性赋值。<property> 标签的 `name` 是属性名，`ref` 是要注入的 bean 对象的 id。**(通过ref属性来完成bean的装配，这是bean最简单的一种装配方式。装配指的是：创建系统组件之间关联的动作)**。这里需要注意的是 `name` 是根据 set 方法来确定而不是类的属性名，而是 set 函数名字去掉 set 然后把第一个字母变小写。为了方面理解不出错应该用规范的 set 命名方法，这样二者就相同了，不容易出错。

&emsp;

### 构造注入

通过构造注入需要在类的方法里面添加构造器，配置文件的声明有两种方法，一是通过下标二是通过参数名。

通过下标：

```xml
<bean id="orderDaoBean" class="com.powernode.spring6.dao.OrderDao"/>
<bean id="userDaoBean" class="com.powernode.spring6.dao.UserDao"/>

<bean id="orderServiceBean" class="com.powernode.spring6.service.OrderService">
  <!--第一个参数下标是0-->
  <constructor-arg index="0" ref="orderDaoBean"/>
  <!--第二个参数下标是1-->
  <constructor-arg index="1" ref="userDaoBean"/>
</bean>
```

通过变量名：

```xml
<bean id="orderDaoBean" class="com.powernode.spring6.dao.OrderDao"/>
<bean id="userDaoBean" class="com.powernode.spring6.dao.UserDao"/>

<bean id="orderServiceBean" class="com.powernode.spring6.service.OrderService">
  <!--这里使用了构造方法上参数的名字-->
  <constructor-arg name="orderDao" ref="orderDaoBean"/>
  <constructor-arg name="userDao" ref="userDaoBean"/>
</bean>
```

&emsp;

## `set()` 方法注入详解

### 内部 Bean 与 外部 Bean

这种做法称为外部 bean，即 bean 定义到外面，在 <property> 标签中使用 `ref` 属性进行注入，通常这种方式是常用的。

```xml
<bean id="orderDao" class="org.example.dao.OrderDao"/>

<bean id="orderService" class="org.example.service.OrderService">
    <property name="orderDao" ref="orderDao"/>
</bean>
```

内部 bean 的方式：在 bean 标签中嵌套 bean 标签。

```xml
<bean id="orderService" class="org.example.service.OrderService">
        <property name="orderDao">
            <bean class="org.example.dao.OrderDao"/>
        </property>
    </bean>
```

&emsp;

### 简单类型的注入

在 Spring 框架中，认为以下类型是简单类型：

- 基本数据类型
- 基本数据类型对应的包装类
- String 或其他的 CharSequence 子类
- Number 子类
- Date 子类
- Enum 子类
- URI
- URL
- Temporal 子类
- Locale
- Class
- 另外还包括以上简单值类型对应的数组类型。

注意对于简单类型，在注入的时候要使用 `value` 属性，不能使用 `ref`。

```xml
<bean id="userBean" class="com.powernode.spring6.beans.User">
    <property name="age" value="20"/>
</bean>
```

&emsp;

### 注入数组，List 和 Set

当数组中的元素是简单类型：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="person" class="com.powernode.spring6.beans.Person">
        <property name="favariteFoods">
            <array>
                <value>Apple</value>
                <value>Pear</value>
                <value>Blueberry</value>
            </array>
        </property>
    </bean>
</beans>
```

当数组中的元素是非简单类型的时候：

```xml
<bean id="goods1" class="com.powernode.spring6.beans.Goods">
    <property name="name" value="Blueberry"/>
</bean>
<bean id="goods2" class="com.powernode.spring6.beans.Goods">
    <property name="name" value="Apple"/>
</bean>

<bean id="order" class="com.powernode.spring6.beans.Order">
    <property name="goods">
        <array>
            <ref bean="goods1"/>
            <ref bean="goods2"/>
        </array>
    </property>
</bean>
```

基本用法还是和前面一样，如果数组中是简单类型，使用 `value` 标签，反之使用 `ref` 标签。

如果是 List 类型，把 <array> 标签改成 <list> 即可，如果是 Set 就是 <set> 。

&emsp;

### 注入 Map

```xml
<bean id="peopleBean" class="com.powernode.spring6.beans.People">
    <property name="addrs">
        <map>
            <!--如果key不是简单类型，使用 key-ref 属性-->
            <!--如果value不是简单类型，使用 value-ref 属性-->
            <entry key="1" value="北京大兴区"/>
            <entry key="2" value="上海浦东区"/>
            <entry key="3" value="深圳宝安区"/>
        </map>
    </property>
</bean>
```

&emsp;@Autowired注解可以用来注入**非简单类型**

## 基于XML 的自动装配

Spring 还可以完成自动化的注入，自动化注入又被称为自动装配。它可以根据**名字**进行自动装配，也可以根据**类型**进行自动装配。

### 根据名称自动装配

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="orderService" class="org.example.service.OrderService" autowire="byName"/>
    <bean id="orderDao" class="org.example.dao.OrderDao"/>

</beans>
```

在 `orderService` 的标签中新增 `autowire="byName"`，那么对于 OrderDao 的 <bean> 标签的 id 不能随意写，而是应该和对应的 set() 方法名后面保持一致。

&emsp;

### 根据类型自动装配

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--byType表示根据类型自动装配-->
    <bean id="accountService" class="com.powernode.spring6.service.AccountService" autowire="byType"/>
    <bean class="com.powernode.spring6.dao.AccountDao"/>

</beans>
```

在 `accountService` 的标签中新增 `autowire="byType"`，需要注意的是，当 byType 进行自动装配的时候，配置文件中某种类型的 Bean 必须是唯一的，不能出现多个。

**无论是 byName 还是 byType，在装配的时候都是基于 `set()` 方法的。所以 `set()` 方法是必须要提供的**。

&emsp;

## Spring 引入外部属性配置文件

我们都知道编写数据源的时候是需要连接数据库的信息的，例如：driver-url, username, password 等信息。这些信息可以单独写到一个属性配置文件中，这样用户修改起来会更加的方便。

例如首先在类路径下（resources 目录下）新建一个 `jdbc.properties`。

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/spring
username=root
password=root123
```

然后在 Spring 配置文件中引入 context 命名空间，并配置使用 `jdbc.properties` 文件。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="jdbc.properties"/>

    <bean id="dataSource" class="com.powernode.spring6.beans.MyDataSource">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
    </bean>
</beans>
```

&emsp;

## 注解式开发

注解的存在主要是为了简化 XML 的配置。**Spring6 倡导全注解开发**。

### 注解扫描

首先我们来看一个简单的程序，已知包名 `org.example.bean` ，但是不知道他下面有多少个类，其中有些类上有 `@Component` 注解。现在希望通过对包进行扫描，把所有类上有 `@Component` 注解的类实例化，然后放到 `Map` 集合中。

```java
public class ScanComponent {
    public static void main(String[] args) throws Exception {
        // 存放 Bean 的 Map 集合。key 存储 beanId, value 存储 Bean。
        Map<String,Object> beanMap = new HashMap<>();

        String packageName = "com.example.bean";
        String path = packageName.replaceAll("\\.", "/");
        // 通过包名获取绝对路径
        URL url = ClassLoader.getSystemClassLoader().getResource(path);
        File file = new File(url.getPath());
        File[] files = file.listFiles();
        Arrays.stream(files).forEach(f -> {
            String className = packageName + "." + f.getName().split("\\.")[0];
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Component component = clazz.getAnnotation(Component.class);
                    String beanId = component.value();
                    Object bean = clazz.newInstance();
                    beanMap.put(beanId, bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println(beanMap);
    }
}
```

&emsp;

### Spring 中注解的使用

首先需要在配置文件中添加 context 命名空间，并指定要扫描的包：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <context:component-scan base-package="com.example.bean"/>
</beans>
```

在对应的类上加上注解，负责声明 Bean 的注解，常见的包括四个：

- @Component
- @Controller
- @Service
- @Repository

```java
@Component("userBean")
public class User {
}
```

通过源码可以看到，`@Controller`、`@Service`、`@Repository` 这三个注解都是`@Component` 注解的别名，也就是说，这四个注解的功能都一样。用哪个都可以。只是为了增强程序的可读性，建议：在 `Controller` 类上使用 `@Controller`，在 `Service` 类上使用 `@Service`，在 `Dao` 类上使用 `@Repository`。它们都是只有一个 `value` 属性，`value`属性用来指定 bean 的 id，也就是 bean 的名字。如果把 `value` 属性彻底去掉，spring 会给 Bean 自动取名，并且默认名字的规律是 Bean 类名首字母小写即可。

如果是多个包需要被扫描，有两种解决方案：

- 第一种：在配置文件中指定多个包，用逗号隔开。
- 第二种：指定多个包的共同父包。

&emsp;

### 选择性实例化 Bean

假设在某个包下有很多Bean，有的 Bean 上标注了 `@Controller` ，有的标注了`@Component`，有的标注了 `@Service`，有的标注了 `@Repository`，现在由于某种特殊业务的需要，只允许其中所有的 `@Controller` 参与 Bean 管理，其他的都不实例化。这应该怎么办呢？

配置文件做如下调整：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.example.bean" use-default-filters="false">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

</beans>
```

`use-default-filters="true"` 表示：使用 Spring 默认的规则，只要有 `@Component`、`@Controller`、`@Service`、`@Repository` 中的任意一个注解标注，则进行实例化。`use-default-filters="false"` 表示：不再 Spring 默认实例化规则，所有申明的注解全部失效，不再实例化。

```xml
<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
```

 表示只有 `@Controller` 进行实例化。

也可以将 `use-default-filters` 设置为 true（不写就是true），并且采用 `exclude-filter`方式排出哪些注解标注的 Bean 不参与实例化：

```xml
<context:component-scan base-package="com.example.bean">
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Repository"/>
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Service"/>
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

&emsp;

### 属性相关注解

#### @Value

当属性的类型是简单类型时，可以使用 `@Value` 注解进行注入。总共有三种方式实现注入：

```java
// 直接使用在属性上
@Component
public class User {
    @Value(value = "Jack")
    private String name;
    @Value("20")
    private int age;
}

// 使用在 set() 方法上
@Component
public class User {
    private String name;
    private int age;

    @Value("Tom")
    public void setName(String name) {
        this.name = name;
    }

    @Value("30")
    public void setAge(int age) {
        this.age = age;
    }
}

// 使用在构造方法的形参上
@Component
public class User {
    private String name;
    private int age;

    public User(@Value("Ben") String name, @Value("33") int age) {
        this.name = name;
        this.age = age;
    }
}
```

为了简化代码，一般会采用直接在属性上使用 `@Value` 注解完成属性赋值。

&emsp;

#### @Autowired与@Qualifier

`@Autowired` 注解可以用来注入非简单类型，单独使用 `@Autowired` 注解，**默认根据类型装配**。【默认是byType】

源码中有两处需要注意：

* 该注解可以标注在哪里？
  
  构造方法上，方法上，形参上，属性上，注解上
- 该注解有一个 required 属性，默认值是 true，表示在注入的时候要求被注入的 Bean 必须是存在的，如果不存在则报错。如果 required 属性设置为 false，表示注入的 Bean 存在或者不存在都没关系，存在的话就注入，不存在的话，也不报错。

```java
public interface UserDao {
    void insert();
}

@Repository //纳入bean管理
public class UserDaoForMySQL implements UserDao{
    @Override
    public void insert() {
        System.out.println("正在向mysql数据库插入User数据");
    }
}

// 在属性上注入
@Service 
public class UserService {
    @Autowired 
    private UserDao userDao;

    // 没有提供构造方法和setter方法。
    public void save(){
        userDao.insert();
    }
}

// 在 set() 方法上注入
@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.insert();
    }
}

// 在构造方法上
@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.insert();
    }
}

// 构造方法的形参上
@Service
public class UserService {
    private UserDao userDao;

    public UserService(@Autowired UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.insert();
    }
}
```

当有参数的构造方法只有一个时，`@Autowired` 注解可以省略。当然，如果有多个构造方法，`@Autowired` 肯定是不能省略的。在日常开发中，为了保证整齐性和规范性，还是应该都写上。

`@Autowired` 注解默认是 `byType` 进行注入的，也就是说根据类型注入的，如果以上程序中，`UserDao` 接口还有另外一个实现类，则需要 `byName`，根据名称进行装配了。`@Autowired` 注解和 `@Qualifier` 注解联合起来才可以根据名称进行装配，在 `@Qualifier`注解中指定 Bean 名称。

```java
@Repository // 这里没有给bean起名，默认名字是：userDaoForOracle
public class UserDaoForOracle implements UserDao{
    @Override
    public void insert() {
        System.out.println("正在向Oracle数据库插入User数据");
    }
}

@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    @Qualifier("userDaoForOracle") // 这个是bean的名字。
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.insert();
    }
}
```

&emsp;

#### @Resource

`@Resource` 注解也可以完成非简单类型注入，那它和 `@Autowired` 注解有什么区别？

* `@Resource` 注解是 JDK 扩展包中的，也就是说属于 JDK 的一部分，所以该注解是标准注解，更加具有通用性。(JSR-250 标准中制定的注解类型。JSR 是 Java 规范提案。) `@Autowired` 注解是 Spring 框架自己的。

* `@Autowired` 注解默认根据类型装配 `byType`，如果想根据名称装配，需要配合`@Qualifier` 注解一起用。`@Resource` 注解默认根据名称装配 `byName`，未指定名称时，使用属性名作为 `name`。通过 name 找不到的话会自动启动通过类型 `byType` 装配。

* `@Resource` 注解可以用在属性上、`set()` 方法上，`@Autowired` 注解可以用在属性上、`set()` 方法上、构造方法上、构造方法参数上。

需要注意 `@Resource` 注解属于 JDK 扩展包，所以不在 JDK 当中，需要额外引入以下依赖，在 Spring6 中如下 （**Spring6 不再支持 JavaEE，它支持的是 JakartaEE9**）：

```xml
<dependency>
  <groupId>jakarta.annotation</groupId>
  <artifactId>jakarta.annotation-api</artifactId>
  <version>2.1.1</version>
</dependency>
```

&emsp;

### 全注解式开发

所谓的全注解开发就是不再使用 Spring 配置文件了，写一个配置类来代替配置文件。

```java
@Configuration
@ComponentScan({"com.example.web.dao", "com.example.web.service"})
public class Spring6Configuration {
}

@Test
public void testNoXml(){
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Spring6Configuration.class);
    UserService userService = applicationContext.getBean("userService", UserService.class);
    userService.save();
}
```
