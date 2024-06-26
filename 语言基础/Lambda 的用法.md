# Lambda 的使用

参考资料

[Lambda Expressions in Java - YouTube](https://www.youtube.com/watch?v=tj5sLSFjVj4)

&emsp;

## 场景分析

假设我们有一个接口 `Printable` ，其中包含一个抽象方法 `print(String s)`。为了实现并成功调用这个方法，我们之前通常这么写：

```java
// 声明接口
public interface Printable {
    void print(String s);
}

// 定义一个实现类
public class Printer implements Printable {
    @Override
    public void print(String s) {
        System.out.println(s);
    }
}

public static void printThing(Printable p) {
    p.print("hello");
}

// 调用方法
public static void main(String[] args) {
    Printer p = new Printer();
    printThing(p);
}
```

我们的目标是实现并调用 `print()` 方法，因为它是这个接口中定义的唯一一个方法。所有我们希望去做的，或者说真正关心的就是**这个方法是如何具体实现的**，但通过上面可以看出，我们需要先构造一个实现了该接口的类，然后完成类的初始化，最后再去调用，这些步骤过于复杂，且并不是我们真正关心的。Lambda 就是在这种情况下诞生的，它允许我们只去实现对应的方法而避免了无关的繁琐操作。在 Java 中，所有事物都是对象，Lambda 其实就是一个简写的实现接口的匿名对象。

&emsp;

## 函数式接口

在 Java 中，Lambda 的使用有严格的限制，并不是任何情况下都可以，确切的说，它只针对函数式接口处理。**只含有一个抽象方法的接口称为函数式接口**，可以在接口上使用 `@FunctionalInterface`，用于更明显的标识和自动检查。例如常用的 `Comparator` 接口。

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

Lambda 表达式针对的就是函数式接口，相当于给这个接口提供实现类的对象，但是提供了更为简洁的方式。

### Lambda 和 匿名内部类的区别

* 匿名内部类可以是接口，抽象类或者具体类，Lambda 表达式只能是接口。

* 如果接口中有多个抽象方法则只能使用匿名内部类。

* 匿名内部类编译之后会生成单独一个 `.class` 字节码文件，Lambda 表达式编译之后没有。

&emsp;

## Lambda 表达式语法

### 基本写法

Lambda 用符号 `->` 表示，左边是参数，通过 () 包裹，和定义的接口保持一致，但是参数类型可以不写；右边就是具体的逻辑实现，通过 {} 包裹，如果逻辑只有一行则也可以省略。总结来说，就是`(形参列表) -> {方法体}` 根据这个可以改写一下上面的 `printThing()` 的调用：

```java
@FunctionalInterface
public interface Printable {
    void print(String s);
}

public static void printThing(Printable p) {
    p.print("hello");
}

public static void main(String[] args) {
    printThing((s) -> {System.out.println(s)});
}
```

如果想理解的更清楚，它其实是这样的：

```java
public static void main(String[] args) {
    Printable lambdaP = (s) -> {System.out.println(s)};
    printThing(lambdaP);
}
```

这个也符合之前对 Lambda 的定义，相当于给这个接口提供实现类的对象。

&emsp;

### 基本使用

1. 无参数无返回值
   
   ```java
   () -> System.out.println("hello");
   ```

2. 有一个参数无返回值
   
   ```java
   (s) -> System.out.println(s);
   ```

3. 无参数有一个返回值
   
   ```java
   () -> return 5;
   ```

4. 有一个参数有一个返回值
   
   ```java
   (num) -> return num * 2;
   ```

语法省略的相关细节：

* 形参的类型可以省略，如果省略每个形参类型都要省略。

* 如果只有一个形参，形参类型和小括号都可以省略。

* 如果方法体中只有一个语句，大括号可以省略。

&emsp;

## Java 内置的基本函数式接口

Java 官方提供了一些函数式接口，都定义在 `java.util.function` 中。

### Function

`Function<T, R>` takes two type parameters, `<T>` is the type of the input, `<R>` is the type of the output. The functional method is `R apply(T t)`, which takes one parameter and then output.

```java
Function<Integer, Integer> addOneFunc = num -> num + 1;
```

There are two default methods inside this interface. One is `addThen()`, the other is `compose()`. Both of those two methods take another `Function` as the input parameter to form a chain of `Function`. The only difference is that for `addThen()`, it will execute the input function parameter later, while for `compose()`, it will execute the input function parameter first.

```java
public void functionDemo() {
    Function<Integer, Integer> addOneFunc = num -> num + 1;
    Function<Integer, Integer> multiplyByTenFunc = num -> num * 10;
    Function<Integer, Integer> addOneAndMultiplyByTen = addOneFunc.andThen(multiplyByTenFunc);
    Function<Integer, Integer> MultiplyByTenAndAddOne = addOneFunc.compose(multiplyByTenFunc);

    Assert.assertEquals(Integer.valueOf(50), addOneAndMultiplyByTen.apply(4));
    Assert.assertEquals(Integer.valueOf(41), MultiplyByTenAndAddOne.apply(4));
}
```

### BiFunction

`BiFunction<T, U, R>` represents a function that accepts two arguments and produces a result. `<T>` is the type of the first argument to the function, `<U>` is the type of the second argument to the function, and `<R>` is the type of the result of the function. This is the two-arity specialization of Function.

```java
BiFunction<Integer, Integer, Integer> addTwoIntegers = (num1, num2) -> num1 + num2;
```

In `BiFunction`, it only has `addThen()` default method.

### Consumer

`Consumer<T>` represents an operation that accepts a single input argument and returns no result. Unlike most other functional interfaces, Consumer is expected to operate via side-effects. `<T>` is the type of the input to the operation. The functional method is `void accept(T t)`, which performs this operation on the given argument.

```java
Consumer<Integer> printConsumer = num -> System.out.println(num);
```

There is a default method inside this interface, `addThen()`, which input parameter is another `Consumer` and it will be executed next.

`Consumer` is quite similar to `Runnable`, the difference is that the funtional method in `Runnable` does not take any input parameters. (`void run()`).

### Predicate

`Predictate<T>` represents a predicate (boolean-valued function) of one argument. `<T>` is the type of the input to the operation. The functional method is `boolean test(T t)`, which evaluates this predicate on the given argument.

```java
Predicate<Integer> isLargerThanFive = num -> num > 5;
```

There are several default methods to form a composed predicate that represent new logic. For example, there are `and()` (similar to AND), `negate()` (similar to NOT), `or()` (similar to OR), and so on.

### Supplier

`Supplier<T>` represents a supplier of results. `<T>` is the type of results supplied by this supplier. There is no requirement that a new or distinct result be returned each time the supplier is invoked. The functional method is `T get()`, which simply get a result.

```java
Supplier<Integer> constantSupplier = () -> 4;
```

&emsp;

## 方法引用

* 实例方法的引用
  
  语法：`对象名称 :: 实例方法`
  
  在 Lambda 表达式中，通过对象来调用某个指定的方法。

* 静态方法的引用
  
  语法：`类名称 :: 静态方法`
  
  在 Lambda 表达式中，通过类名来调用某个指定的静态方法。

&emsp;

## 常见使用

### 比较大小

在涉及到堆的算法题中，常常需要自定义排序方法，确定使用最大堆还是最小堆：

```java
// 不使用 Lambda
PriorityQueue<int[]> pq = new PriorityQueue<>(10, new Comparator<int[]>(){
    @Override
    public int compare(int[] i1, int[] i2) {
        int dist1 = i1[0] * i1[0] + i1[1] * i1[1];
        int dist2 = i2[0] * i2[0] + i2[1] * i2[1];
        return Integer.compare(dist1, dist2);
    }
});

// 使用 Lambda
PriorityQueue<int[]> pq = new PriorityQueue<>(10, (i1, i2) -> {
    int dist1 = i1[0] * i1[0] + i1[1] * i1[1];
    int dist2 = i2[0] * i2[0] + i2[1] * i2[1];
    return Integer.compare(dist1, dist2);
});
```

&emsp;

### 集合遍历

`Collection` 集合和 `Map` 集合都提供了 `forEach()` 方法用于遍历集合，方法的形参是 `Consumer` 接口。

```java
list.forEach(num -> System.out.println(num));
map.forEach((k, v) -> System.out.println(k + ":" + v));
```
