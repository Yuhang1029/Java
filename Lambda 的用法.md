# Lambda 的使用

参考资料

[Lambda Expressions in Java - YouTube](https://www.youtube.com/watch?v=tj5sLSFjVj4)

&emsp;

## 场景分析

假设我们有一个接口 Printable ，其中包含一个抽象方法 print(String s)。为了实现并成功调用这个方法，我们之前通常这么写：

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

在 Java 中，Lambda 的使用有严格的限制，并不是任何情况下都可以，确切的说，它只针对函数式接口处理。只含有一个抽象方法的接口称为函数式接口，可以在接口上使用 `@FunctionalInterface`，用于更明显的标识和自动检查。例如常用的 `Comparator` 接口。

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

Lambda 表达式针对的就是函数式接口，相当于给这个接口提供实现类的对象，但是提供了更为简洁的方式。

&emsp;

## Lambda 表示式写法

Lambda 用符号 `->` 表示，左边是参数，通过 () 包裹，和定义的接口保持一致，但是参数类型可以不写；右边就是具体的逻辑实现，通过 {} 包裹，如果逻辑只有一行则也可以省略。根据这个可以改写一下上面的 `printThing()` 的调用：

```java
@FunctionalInterfacepublic 
interface Printable {
    void print(String s);
}

public static void printThing(Printable p) {
    p.print("hello");
}

public static void main(String[] args) {
    printThing((s) -> System.out.println(s));
}
```

如果想理解的更清楚，它其实是这样的：

```java
public static void main(String[] args) {
    Printable lambdaP = (s) -> System.out.println(s);
    printThing(lambdaP);
}
```

这个也符合之前对 Lambda 的定义，相当于给这个接口提供实现类的对象。

&emsp;

## 常见使用

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
