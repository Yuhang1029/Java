# Stream API 的使用

## 概述

从 Java 8 开始， Java 语言引入了一个全新的流式 Stream API， 把真正的函数式编程风格运用到 Java 语言中。使用 Stream API 可以帮我们更方便的操作集合，使得代码更加简洁，易于维护。和 Collection 相比，Collection 面向的是内存，存储在内存中；Stream API 面向的是 CPU，关注的是计算。

Stream API 的操作步骤有三个：

1. 通过数据源（如：集合，数组等）来获取一个 Stream 对象。

2. 对数据源的数据进行处理，该操作会返回一个 Stream 对象，因此可以进行链式操作。

3. 执行终止操作时，才会真正执行中间操作，并且返回一个计算完毕的结果。

Stream API 的重要特点包括：

* Stream 不会自己存储元素，只能对元素进行计算。

* Stream 不会改变数据对象，只会返回一个持有结果的新的 Stream。

* Stream 上的操作属于延迟执行，只有等用户真正需要才会执行。

* 一旦停止操作，不能在调用其他中间操作或终止操作了。

&emsp;

## 创建 Stream 的方式

**通过 Collection 接口提供的方法：**

```java
List<String> list = new ArrayList<>();
list.add("aaa");
list.add("bbb");
Stream<String> stream = list.stream();
```

**通过 Arrays 类提供的方法：**

```java
String[] arr = {"aaa", "bbb"};
Stream<String> stream = Arrays.stream(arr);
```

**通过 Stream 接口提供的方法：**

```java
Stream<String> stream = Stream.of("aaa", "bbb");
```

&emsp;

## Stream API 的中间操作

**filter()** 

筛选出符合条件的元素并提取到新的流操作中。该操作使用了 `Predicate<? super T> predict()` 接口实现。

```java
stream.filter(str -> str.length() > 3).forEach(System.out::println);
```

**map()**

将元素按照一定的映射规则映射到另一个流中，该操作使用了 Stream 接口 提供的 `map(Function<? super T, ? extends R> mapper)` 来实现。
