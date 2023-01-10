# Spring 对事务的支持

## 事务概述

- 什么是事务 Transaction：
  
  - 在一个业务流程当中，通常需要多条 DML（insert delete update）语句共同联合才能完成，这多条 DML 语句必须同时成功，或者同时失败，这样才能保证数据的安全。
  
  - 多条 DML 要么同时成功，要么同时失败，这叫做事务。

- 事务的四个处理过程：
  
  - 第一步：开启事务 (start transaction)
  
  - 第二步：执行核心业务代码
  
  - 第三步：提交事务（如果核心业务处理过程中没有出现异常）(commit transaction)
  
  - 第四步：回滚事务（如果核心业务处理过程中出现异常）(rollback transaction)

- 事务的四个特性：
  
  - A 原子性：事务是最小的工作单元，不可再分。
  
  - C 一致性：事务要求要么同时成功，要么同时失败。事务前和事务后的总量不变。
  
  - I 隔离性：事务和事务之间因为有隔离性，才可以保证互不干扰。
  
  - D 持久性：持久性是事务结束的标志。

&emsp;

## Spring 对事务的支持

Spring 实现事务有两种方式：

- 编程式事务：通过编写代码的方式来实现事务的管理。

- 声明式事务：
  
  - 基于注解方式
  
  - 基于 XML 配置方式

下面展示一个注解方式：

配置文件中引入 `tx` 命名空间，配置“事务注解驱动器”，开始注解的方式控制事务。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <context:component-scan base-package="org.example"/>
    <tx:annotation-driven transaction-manager="transactionManager"/>

    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/bank"/>
        <property name="username" value="root"/>
        <property name="password" value="mysql123!"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

</beans>
```

在 `AccountServiceImpl` 类上或方法上添加 `@Transactional` 注解：

```java
@Service("accountService")
@Transactional
public class AccountServiceImpl implements AccountService {
    @Resource(name = "accountDao")
    private AccountDao accountDao;

    @Override
    public void transfer(String fromActno, String toActno, double money) {
        // 查询账户余额是否充足
        Account fromAct = accountDao.selectByActno(fromActno);
        if (fromAct.getBalance() < money) {
            throw new RuntimeException("账户余额不足");
        }
        // 余额充足，开始转账
        Account toAct = accountDao.selectByActno(toActno);
        fromAct.setBalance(fromAct.getBalance() - money);
        toAct.setBalance(toAct.getBalance() + money);
        int count = accountDao.update(fromAct);

        // 模拟异常
        String s = null;
        s.toString();

        count += accountDao.update(toAct);
        if (count != 2) {
            throw new RuntimeException("转账失败，请联系银行");
        }
    }
}
```

通过测试发现，虽然出现异常了，但数据库中表的数据不会变化。

&emsp;

### 全注解式开发

```java
@Configuration
@ComponentScan("org.example.bank")
@EnableTransactionManagement
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

Spring 框架看到 `@Bean` 注解后，会调用这个被标注的方法，这个方法的返回值是一个 Java 对象，这个对象会被自动纳入 IoC 管理。
