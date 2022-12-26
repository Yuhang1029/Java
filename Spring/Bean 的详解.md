# Bean 的详解

## 基础特点

默认情况下，Spring 的 IoC 容器创建的 Bean 对象是单例的，Bean 对象的创建是在初始化 Spring 上下文的时候就完成的。

如果想让 Spring 的 Bean 对象以多例的形式存在，可以在 <bean> 标签中指定 `scope` 属性的值为 `prototype` ，这样Spring会在每一次执行 `getBean()` 方法的时候创建 Bean 对象，调用几次则创建几次。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="sb" class="com.powernode.spring6.beans.SpringBean" scope="prototype" />

</beans>
```

&emsp;

## 实例化方式

### 通过构造方法

这是一个最基础的方法，和前面所做的一样，只需要在配置文件中配置好 <bean>，默认会调用 Bean 的无参数构造方法去实例化。

&emsp;

### 通过简单工厂模式

简单工厂模式就是有一个工厂类，内部包含一个静态方法去构造不同的实例。它的优点是客户端程序不需要关心对象的创建细节，需要哪个对象时，只需要向工厂索要即可，初步实现了责任的分离。客户端只负责“消费”，工厂负责“生产”。生产和消费分离。

```java
public class User {}

public class UserFactory {
    public static User get() {
        return new User();
    }
}
```

然后需要在配置文件中作相应配置，调用哪个类的哪个方法获取 Bean。`factory-method` 指的就是调用这个方法可以获取。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="user" class="org.example.bean.UserFactory" factory-method="get"/>

</beans>
```

&emsp;

### 通过 factory-bean

这种方式本质上是通过工厂方法模式进行实例化。

```java
public class User {}

public class UserFactory {
    public User get() {
        return new User();
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userFactory" class="org.example.bean.UserFactory"/>
    <bean id="user" class="org.example.bean.User" factory-bean="userFactory" factory-method="get"/>

</beans>
```

仔细观察，它和上面的区别就是 `UserFactory` 中的 `get()` 方法不再是一个静态方法，同时在配置文件中也需要明确指明对应生产工厂 `factory-bean`。

&emsp;

### 通过 FactoryBean 接口

上面的第三种方式中，`factory-bean` 是我们自定义的，`factory-method` 也是我们自己定义的。在 Spring 中，当你编写的类直接实现 `FactoryBean` 接口之后，`factory-bean`不需要指定了，`factory-method` 也不需要指定了。`factory-bean` 会自动指向实现`FactoryBean` 接口的类，`factory-method` 会自动指向 `getObject()` 方法。

```java
import org.springframework.beans.factory.FactoryBean;

public class PersonFactoryBean implements FactoryBean<Person> {
    @Override
    public Person getObject() throws Exception {
        return new Person();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        // true 表示单例
        // false 表示原型
        return true;
    }
}
```

这样的话，只需要在配置文件中直接声明就可以了。

```xml
<bean id="personBean" class="org.example.bean.PersonFactoryBean"/>
```

`FactoryBean` 在 Spring 中是一个接口，被称为“工厂 Bean”。“工厂 Bean”是一种特殊的Bean。所有的“工厂 Bean”都是用来协助 Spring 框架来创建其他 Bean 对象的。

&emsp;

## Bean 的生命周期

Spring 其实就是一个管理 Bean 对象的工厂。它负责对象的创建，对象的销毁等。所谓的生命周期就是对象从创建开始到最终销毁的整个过程，换句话说在哪个时间节点上调用了哪个类的哪个方法。因为我们可能需要在某个特殊的时间点上执行一段特定的代码，这段代码就可以放到这个节点上，当生命线走到这里的时候，自然会被调用。

&emsp;

### 粗略的5步周期

Bean生命周期可以粗略的划分为五大步：

![image.png](https://cdn.nlark.com/yuque/0/2022/png/21376908/1665388735200-444405f6-283d-4b3a-8cdf-8c3e01743618.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_24%2Ctext_5Yqo5Yqb6IqC54K5%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10)

例如下面的程序：

```java
public class User {
    private String name;

    public User() {
        System.out.println("1.实例化Bean");
    }

    public void setName(String name) {
        this.name = name;
        System.out.println("2.Bean属性赋值");
    }

    public void initBean(){
        System.out.println("3.初始化Bean");
    }

    public void destroyBean(){
        System.out.println("5.销毁Bean");
    }
}
```

配置文件中也需要单独声明：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--
    init-method属性指定初始化方法。
    destroy-method属性指定销毁方法。
    -->
    <bean id="userBean" class="org.example.bean.User" init-method="initBean" destroy-method="destroyBean">
        <property name="name" value="zhangsan"/>
    </bean>

</beans>
```

同时注意在测试文件中需要关闭 Spring 容器才会执行销毁方法：

```java
public class BeanLifecycleTest {
    @Test
    public void testLifecycle(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        User userBean = applicationContext.getBean("userBean", User.class);
        System.out.println("4.使用Bean");
        // 只有正常关闭 spring 容器才会执行销毁方法
        ClassPathXmlApplicationContext context = (ClassPathXmlApplicationContext) applicationContext;
        context.close();
    }
}
```

最后输出顺序就是 1-2-3-4-5。

&emsp;

### 完整的10步生命周期

如果你还想在初始化前和初始化后添加代码，可以加入“Bean后处理器”。需要编写一个类实现 `BeanPostProcessor` 接口，并且重写两个方法：

```java
public class LogBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Bean 后处理器的 before 方法执行，即将开始初始化");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Bean 后处理器的 after 方法执行，已完成初始化");
        return bean;
    }
}
```

<img src="https://cdn.nlark.com/yuque/0/2022/png/21376908/1665394697870-15de433a-8d50-4b31-9b75-b2ca7090c1c6.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_12%2Ctext_5Yqo5Yqb6IqC54K5%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10" title="" alt="image.png" data-align="center">

完整的十步如上图，黄色部分使用很少，了解即可。

需要注意的是，Spring 会根据 Bean 的作用域来选择管理方式。对于 singleton 作用域的Bean，Spring 能够精确地知道该 Bean 何时被创建，何时初始化完成，以及何时被销毁，而对于 prototype 作用域的 Bean，Spring 只负责创建，当容器创建了 Bean 的实例后，Bean 的实例就交给客户端代码管理，Spring 容器将不再跟踪其生命周期。

&emsp;

## 循环依赖

A 对象中有 B 属性。B 对象中有 A 属性。这就是循环依赖。

在 singleton+set 的模式下产生的循环依赖，Spring 可以正常解决，而 prototype+set 和singleton + 构造注入 两种模式下，Spring 都会报错。究其原因，**Spring 将“实例化Bean”和“给Bean属性赋值”这两个动作分开去完成**。实例化 Bean 的时候，调用无参数构造方法来完成。此时可以先不给属性赋值，可以提前将该Bean对象“曝光”给外界。给Bean属性赋值的时候，调用 `set()` 方法来完成。两个步骤是完全可以分离开去完成的，并且这两步不要求在同一个时间点上完成。也就是说，Bean 都是单例的，我们可以先把所有的单例Bean 实例化出来，放到一个集合当中（我们可以称之为缓存），所有的单例 Bean 全部实例化完成之后，以后我们再慢慢的调用 `set()` 方法给属性赋值。这样就解决了循环依赖的问题。
