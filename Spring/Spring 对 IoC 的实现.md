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

&emsp;

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
