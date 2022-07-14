# Optional 的使用

参考资料

[Optionals In Java - Simple Tutorial - YouTube](https://www.youtube.com/watch?v=vKVzRbsMnTQ)

[了解、接受和利用 Java 中的 Optional - 简书](https://www.jianshu.com/p/63830b7cb743)

&emsp;

## 场景介绍

首先我们介绍一个非常常见的数据库查询场景，假设我们有一个数据库的表 `Student` 代表班级里的所有学生，每一个学生包含姓名，年龄，性别等字段。当我们需要根据某一个条件查询学生，然后打印学生的相关信息时，我们常常会做下面的处理操作：

```java
@Service
public class StudentService {
    private StudentDao studentDao;
    
    @Autowired
    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public void findStudentByName(String name) {
        Student s = this.studentDao.findByName(name);
        if (s != null) {
            System.out.println(s.getAge());
        }
    }
}
```

如果学生的信息被成功找到，那么打印他的年龄不会出现任何的问题，但是很多时候我们查询数据库时并不能确保相应的信息是存在的，即返回上来的值可能是 `null`。为了避免出现 `NullPointerException` ，很多时候我们需要繁琐的检验当前是否为 `null` 。`Optional` 就是针对这种情况而产生的，当我们无法确定返回的是一个实例还是空的时候，就可以使用它了。当使用它作为返回类型时，其实就是明确告诉可能存在 `null` 的情况从而需要特殊考虑。`Optional` 形象的来说可以看作是一个容器，里面既可以装着实体类，也可以是空的，代表 `null`。

&emsp;

## 常用 API

**新建 Optional**

```java
Optional<String> empty = Optional.empty();
Optional<String> exist = Optional.of("name");
Optional<String> existOrNot = Optional.ofNullable("name");
```

第一个是创建一个空的 `Optional` 对象；第二个是必须传入一个非 `null` 参数，否则将引发空指针异常；如果我们不知道参数是否为null，那就是我们使用 `ofNullable` 的时候，这时候即使传入一个空引用，它不会抛出异常，而是返回一个空的 `Optional` 对象。

&emsp;

**判断是否存在值与获取值**

```java
Optional<String> optional = Optional.of("java");
if (optional.isPresent()){ 
  String value = optional.get();
}
String value = optional.orElse("one");
```

通过 `isPresent()` 判断 `Optional` 对象中是否存在值，如果存在可以通过 `get()` 方法获取。此外，如果当类是空的时候需要使用默认值也可以用 `orElse()`。

&emsp;

## 最佳实践

`Optional` 的方法是尝试通过增加构建更具表现力的 API 的可能性来减少 Java 系统中空指针异常的情况，这些 API 解释了有时缺少返回值的可能性。如果从一开始就存在 `Optional`，那么大多数库和应用程序可能会更好地处理缺少的返回值，从而减少了空指针异常的数量以及总体上的错误总数。`Optional` 的预期用途**主要是作为返回类型**。获取此类型的实例后，可以提取该值（如果存在）或提供其他行为（如果不存在），就如同一开始举的例子一样。相应的一开始的代码可以更改成：

```java
@Service
public class StudentService {
    private StudentDao studentDao;
    
    @Autowired
    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public void findStudentByName(String name) {
        Optional<Student> s = this.studentDao.findByName(name);
        if (s.isPresent()) {
            System.out.println(s.get().getAge());
        }
    }
}
```

要注意的是，`Optional` 并不意味着是一种避免所有类型的空指针的机制，它仍需要在提取时对内部判断或者使用默认值。
