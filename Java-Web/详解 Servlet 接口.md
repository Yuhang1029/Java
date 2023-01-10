# 详解 Servlet 接口

## Servlet 的生命周期

Servlet 对象的生命周期表示一个 Servlet 对象从创建到最后的销毁，整个过程是怎样的。需要注意的是，Servlet 对象的生命周期是由 Tomcat 服务器（WEB Server）全权负责的，又称为 WEB 容器。Servlet 对象的创建，方法的调用，最终的销毁，Java Web 程序员是无权干预的。但是，我们自己 new 的 Servlet 对象是不受 WEB 容器管理的。因为 WEB 容器创建的Servlet 对象，这些 Servlet 对象都会被放到一个集合当中（HashMap），只有放到这个HashMap 集合中的 Servlet 才能够被 WEB 容器管理。

首先来看一下 `Servlet` 接口，里面定义了五个方法，其中 `service()` 方法负责接收具体的请求并且做出响应。

```java
public interface Servlet {
    void init(ServletConfig var1) throws ServletException;
    ServletConfig getServletConfig();
    void service(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
    String getServletInfo();
    void destroy();
}
```

在上面介绍 `Servlet` 接口的定义方法里面，有两个与生命周期有关的方法，`init()` 和 `destroy()`，对应新建与销毁。默认情况下，`Servlet` **在第一次接收到请求的时候才创建对象**，进行实例化（调用构造方法）和初始化（调用 `init()`），创建对象后，所有的 URL 地址匹配的请求都由这同一个对象来处理，后面的每一次请求都会去调用 `service()` 方法。当容器关闭时，会调用 `destroy()`来销毁。需要注意的是，`destroy()` 方法执行的时候 Servlet 对象还在，没有被销毁。`destroy()` 方法执行结束之后，Servlet 对象的内存才会被 Tomcat 释放。

综上所述，关于 Servlet 类中方法的调用次数：

- 构造方法只执行一次。
- `init()` 方法只执行一次，紧跟着构造方法后调用。
- `service()` 方法：用户发送一次请求则执行一次，发送N次请求则执行N次。
- `destroy()` 方法只执行一次。

在 Tomcat 中，每一个请求会被分配一个线程来处理，所以可以说，`Servlet` 是**单实例，多线程方式运行**的。既然 `Servlet` 是多线程方式运行，所以有线程安全方面的可能性，所以不能在处理请求的方法中修改成员变量的值。

如果希望在初始化的时候做一些准备工作，可以重写 `init()` ，而不是构造函数，这是因为Servlet 规范中有要求。作为 Java Web 程序员，编写 Servlet 类的时候，如果编写构造方法，很容易让无参数构造方法消失，这个操作可能会导致 Servlet 对象无法实例化。所以 `init()` 方法是有存在的必要的。

&emsp;

## GenericServlet 详解

如果我们编写一个 Servlet 类直接实现 Servlet 接口，会发现我们只需要 `service()` 方法，其他方法大部分情况下是不需要使用的，但是仍需要我们手动实现，代码很丑陋。在 JakartaEE 中，设计者想到了一个很好的解决方法，就是适配器设计模式。编写一个 `GenericServlet`类，这个类是一个抽象类，其中有一个抽象方法 `service()`。`GenericServlet` 实现 `Servlet` 接口，以后编写的所有 Servlet 类继承 `GenericServlet`，重写 `service()` 方法即可。

在 `GenericServlet` 中有两个 `init()` 方法，如下：

```java
public void init(ServletConfig config) throws ServletException {
    this.config = config;
    this.init();
}

public void init() throws ServletException {
}
```

在后面的继承中，我们只需要实现第二个即可，这样保证 `ServletConfig` 也可以正常赋值。Tomcat 服务器会先创建 `ServletConfig` 对象，然后调用 `init()` 方法，将`ServletConfig` 对象传给了对应 `Servlet` 类。

&emsp;

## ServletConfig 详解

```java
public interface ServletConfig {
    String getServletName();
    ServletContext getServletContext();
    String getInitParameter(String var1);
    Enumeration<String> getInitParameterNames();
}
```

`ServletConfig` 是 `Servlet` 对象的配置信息对象，它里面封装了标签中的配置信息，即`web.xml` 文件中 servlet 的配置信息，一个 `Servlet` 对象对应一个 `ServletConfig` 对象。之前学过，`Servlet` 对象是 Tomcat 服务器创建，`ServletConfig` 对象也是 Tomcat 服务器创建，并且默认情况下，他们都是在用户发送第一次请求的时候创建。

和上面的 `Servlet` 接口一样，这个接口也被 `GenericServlet` 实现，所以在开发中不会直接继承 `ServletConfig` 接口。

&emsp;

## ServletContext 详解

上面学过，一个 `Servlet` 对象对应一个 `ServletConfig`，100个 `Servlet` 对象则对应100个`ServletConfig` 对象。**对于 `ServletContext` ，只要在同一个 WebApp 当中，只要在同一个应用当中，所有的 `Servlet` 对象都是共享同一个 `ServletContext` 对象的**。

`ServletContext` 直译的话叫做“Servlet 上下文，听着挺别扭。它其实就是个大容器，是个map。一个 `ServletContext` 对象通常对应的是一个 `web.xml` 文件，服务器会为每个应用创建一个 `ServletContext` 对象：

- `ServletContext` 对象的创建是在服务器启动时完成的
- `ServletContext` 对象的销毁是在服务器关闭时完成的

`ServletContext` 对象的作用是在整个 Web 应用的动态资源（Servlet / JSP）之间共享数据。例如在 A Servlet 中向 `ServletContext` 对象保存一个值，然后在 B Servlet 中就可以获取这个值。

实际上，`ServletContext` 是一个接口，Tomcat 服务器对 `ServletContext` 接口进行了实现。`ServletContext` 实现对象的创建也是由 Tomcat 服务器来完成的，会在启动 WebApp 的时候创建的。

&emsp;

### ServletContext 的使用

`ServletContext` 中常用方法有：

```java
public String getInitParameter(String name); // 通过初始化参数的name获取value
public Enumeration<String> getInitParameterNames(); // 获取所有的初始化参数的name
```

以上两个方法是 `ServletContext` 对象的方法用来方法获取以下的配置信息：

```xml
<context-param>
    <param-name>pageSize</param-name>
    <param-value>10</param-value>
</context-param>
<context-param>
    <param-name>startIndex</param-name>
    <param-value>0</param-value>
</context-param>
```

<!--注意：以上的配置信息属于应用级的配置信息，一般一个项目中共享的配置信息会放到以上的标签当中。-->

**注意，以上的配置信息属于应用级的配置信息，一般一个项目中共享的配置信息会放到以上的标签**，当中如果你的配置信息只是想给某一个 servlet 作为参考，那么你配置到 servlet 标签当中即可，使用ServletConfig对象来获取。

此外，利用 ServletContext 可以获取应用的根路径（非常重要），因为在 Java 源代码当中有一些地方可能会需要应用的根路径，这个方法可以动态获取应用的根路径。

```java
public String getContextPath();
```

`ServletContext` 第三个核心作用就是入 Context 名字所体现的那样，即作用域。如果所有的用户共享一份数据，并且这个数据很少的被修改，并且这个数据量很少，可以将这些数据放到 `ServletContext` 这个应用域中。

* 为什么是所有用户共享的数据？ 不是共享的没有意义。因为 `ServletContext` 这个对象只有一个，只有共享的数据放进去才有意义。

* 为什么数据量要小？ 因为数据量比较大的话，太占用堆内存，并且这个对象的生命周期比较长，服务器关闭的时候，这个对象才会被销毁。大数据量会影响服务器的性能。占用内存较小的数据量可以考虑放进去。

* 为什么这些共享数据很少的修改，或者说几乎不修改？所有用户共享的数据，如果涉及到修改操作，必然会存在线程并发所带来的安全问题。所以放在 `ServletContext` 对象中的数据一般都是只读的。

数据量小、所有用户共享、又不修改，这样的数据放到 `ServletContext` 这个应用域当中，会大大提升效率。因为应用域相当于一个缓存，放到缓存中的数据，下次在用的时候，不需要从数据库中再次获取，大大提升执行效率。

```java
// 存（怎么向 ServletContext 应用域中存数据）
public void setAttribute(String name, Object value); 
// 取（怎么从 ServletContext 应用域中取数据）
public Object getAttribute(String name); 
// 删（怎么删除 ServletContext 应用域中的数据）
public void removeAttribute(String name); 
```

&emsp;

## HTTPServlet 详解

`HttpServlet` 类是专门为 HTTP 协议准备的，它在 `jakarta.servlet.http.HttpServlet` 包下，比 `GenericServlet` 更加适合 HTTP 协议下的开发。在这个包下，除了 `HttpServlet` 这个抽象类，还包括 `HttpServletRequest` （HTTP 协议专用的请求对象）和 `HttpServletResponse` （HTTP 协议专用的响应对象）。`HttpServletRequest` 对象中封装了请求协议的全部内容，Tomcat 服务器（WEB 服务器）将请求协议中的数据全部解析出来，然后将这些数据全部封装到 request 对象当中了。也就是说，我们只要面向`HttpServletRequest`，就可以获取请求协议中的数据。`HttpServletResponse` 对象是专门用来响应 HTTP 协议到浏览器的。

&emsp;

### 源码分析

`HttpServlet` 依然是一个抽象类，它继承了 `GenericServlet`。

```java
public abstract class HttpServlet extends GenericServlet {}
```

在 `GenericServlet` 中，它定义了 `service()` 方法，但是仍然没有去实现：

```java
public abstract void service(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
```

在 `HttpServlet` 中，里面有两个 `service()` 方法：

```java
public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        this.service(request, response);
    } else {
        throw new ServletException("non-HTTP request or response");
    }
}
```

在上面第一个方法中，可以看出这里并没有实现任何逻辑，只是进行了类型转换，然后就调用第二个 `service()` 方法，参数类型也从 `ServletRequest` 变成了 `HttpServletRequest`。

```java
protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String method = req.getMethod();
    long lastModified;
    if (method.equals("GET")) {
        lastModified = this.getLastModified(req);
        if (lastModified == -1L) {
            this.doGet(req, resp);
        } else {
            long ifModifiedSince = req.getDateHeader("If-Modified-Since");
            if (ifModifiedSince < lastModified) {
                this.maybeSetLastModified(resp, lastModified);
                this.doGet(req, resp);
            } else {
                resp.setStatus(304);
            }
        }
    } else if (method.equals("HEAD")) {
        lastModified = this.getLastModified(req);
        this.maybeSetLastModified(resp, lastModified);
        this.doHead(req, resp);
    } else if (method.equals("POST")) {
        this.doPost(req, resp);
    } else if (method.equals("PUT")) {
        this.doPut(req, resp);
    } else if (method.equals("DELETE")) {
        this.doDelete(req, resp);
    } else if (method.equals("OPTIONS")) {
        this.doOptions(req, resp);
    } else if (method.equals("TRACE")) {
        this.doTrace(req, resp);
    } else {
        String errMsg = lStrings.getString("http.method_not_implemented");
        Object[] errArgs = new Object[]{method};
        errMsg = MessageFormat.format(errMsg, errArgs);
        resp.sendError(501, errMsg);
    }
}
```

在这个方法中，可以看出 `service()` 是一个模版方法，在该方法中定义核心算法骨架，具体的实现步骤延迟到子类中去完成。它的第一步是判断请求类型，然后根据不同类型去调用不同的 `doXXX()` 方法。以 `doGet()` 为例，它只是会返回当前方法没有被支持，默认按照 405 的错误去报错，所以为什么在自定义的 `Servlet` 实现类中要根据自己需要的请求方式，自己实现相应的方法。

```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String protocol = req.getProtocol();
    String msg = lStrings.getString("http.method_get_not_supported");
    resp.sendError(this.getMethodNotSupportedCode(protocol), msg);
}
```

&emsp;

### 自定义 Servlet 开发步骤

- 第一步：编写一个 `Servlet` 类，直接继承 `HttpServlet`。
- 第二步：重写 `doGet()` 方法或者重写 `doPost()` 方法，到底重写谁，Java Web 程序员说了算。
- 第三步：将 `Servlet` 类配置到 `web.xml` 文件当中。
- 第四步：准备前端的页面（form表单），form 表单中指定请求路径即可。

&emsp;

## HttpServletRequest 详解

前面介绍过，`HttpServletRequest` 对象是 Tomcat 服务器负责创建的，这个对象中封装了HTTP 的请求协议。实际上，用户发送请求的时候，遵循了 HTTP 协议，发送的是 HTTP 的请求协议，Tomcat 服务器将 HTTP 协议中的信息以及数据全部解析出来，然后 Tomcat 服务器把这些信息封装到 `HttpServletRequest` 对象当中，传给了程序员，程序员面向`HttpServletRequest` 接口编程，调用方法就可以获取到请求的信息了。

接口中常用的方法有：

```java
Enumeration<String> getParameterNames()  // 这个是获取 Map 集合中所有的 key
String[] getParameterValues(String name) // 根据 key 获取 Map 集合的 value
String getParameter(String name)         // 获取 value 这个一维数组当中的第一个元素,这个方法最常用
```

&emsp;

### 请求域对象

`HttpServletRequest` 继承于 `ServletRequest`，和 `ServletContext` 类似，里面也有三个方法：

```java
void setAttribute(String name, Object obj); // 向域当中绑定数据。
Object getAttribute(String name); // 从域当中根据 name 获取数据。
void removeAttribute(String name); // 将域当中绑定的数据移除
```

请求域对象要比应用域对象范围小很多，生命周期短很多。请求域只在一次请求内有效，一个请求对象 request 对应一个请求域对象，一次请求结束之后，这个请求域就销毁了。

&emsp;

### 其他常用方法

获取客户端的IP地址

```java
String remoteAddr = request.getRemoteAddr();
```

获取应用的根路径

```java
String contextPath = request.getContextPath();
```

获取请求方式

```java
String method = request.getMethod();
```

获取请求的 URI

```java
String uri = request.getRequestURI(); 
```

获取 servlet path

```java
String servletPath = request.getServletPath(); 
```

&emsp;

## 请求转发与重定向

发一个请求给 `Servlet`，接力棒就传递到了 `Servlet` 手中。而绝大部分情况下，`Servlet` 不能独自完成一切，需要把接力棒继续传递下去，此时我们就需要请求的转发或重定向。

&emsp;

### 转发

完整定义：在请求的处理过程中，`Servlet` 完成了自己的任务，需要把请求转交给下一个资源继续处理。**由于转发操作的核心部分是在服务器端完成的，所以浏览器感知不到，整个过程中浏览器只发送一次请求**。因为是同一个请求，所以可以访问到相同的请求域里的内容，其实就实现了两个 Servlet 之间共享数据。

```java
request.getRequestDispatcher("/apple.html").forward(request, response);
```

&emsp;

### 重定向

完整定义：在请求的处理过程中，`Servlet` 完成了自己的任务，然后**以一个响应的方式告诉浏览器**，要完成这个任务还需要你另外再访问下一个资源。**由于重定向操作的核心部分是在浏览器端完成的，所以整个过程中浏览器共发送两次请求**。

```java
response.sendRedirect("/apple.html");
```

重定向有两种：一种是302响应，称为临时重定向，一种是301响应，称为永久重定向。两者的区别是，如果服务器发送301永久重定向响应，浏览器会缓存`/hi`到`/hello`这个重定向的关联，下次请求`/hi`的时候，浏览器就直接发送`/hello`请求了。

重定向有什么作用？重定向的目的是当 Web 应用升级后，如果请求路径发生了变化，可以将原来的路径重定向到新路径，从而避免浏览器请求原路径找不到资源。

`HttpServletResponse`提供了快捷的`redirect()`方法实现302重定向。如果要实现301永久重定向，可以这么写：

```java
resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
resp.setHeader("Location", "/hello");
```

&emsp;

### 两者区别

- 转发：是由 WEB 服务器来控制的。A 资源跳转到 B 资源，这个跳转动作是 Tomcat 服务器内部完成的。
- 重定向：是浏览器完成的。针对第一个请求服务器会把新的路径以 response 的形式发回给浏览器，浏览器再去请求第二个资源。具体跳转到哪个资源，是浏览器说了算。

转发和重定向应该如何选择呢？如果在上一个 Servlet 当中向 request 域当中绑定了数据，希望从下一个 Servlet 当中把 request 域里面的数据取出来，使用转发机制，剩下所有的请求均使用重定向。（重定向使用较多）

需要注意，跳转的下一个资源只要是服务器内部合法的资源即可，包括 Servlet、JSP、HTML.....，不一定只能是 Servlet。

&emsp;

## 注解开发

Servlet 3.0 版本之后，推出了各种 Servlet 基于注解式开发，优点是开发效率高，不需要编写大量的配置信息。直接在 Java 类上使用注解进行标注，`web.xml `文件体积变小了。注意这里并不是说注解有了之后，`web.xml` 文件就不需要了，有一些需要变化的信息，还是要配置到 `web.xml` 文件中。一般都是 注解+配置文件 的开发模式。一些不会经常变化修改的配置建议使用注解。一些可能会被修改的建议写到配置文件中。

在 Servlet 类上使用 `@WebServlet`，其中包含属性：

* `name` 属性：用来指定 Servlet 的名字。

* `urlPatterns` 属性：用来指定 Servlet 的映射路径。可以指定多个字符串。

* `loadOnStartUp` 属性：用来指定在服务器启动阶段是否加载该 Servlet。

不是必须将所有属性都写上，只需要提供需要的。注意当属性是一个数组，如果数组中只有一个元素，使用该注解的时候，属性值的大括号可以省略。

注解对象的使用格式：@注解名称(属性名=属性值, 属性名=属性值, 属性名=属性值....)
