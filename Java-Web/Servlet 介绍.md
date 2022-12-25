# Servlet 介绍

**参考文档**

[Servlet 入门](https://www.liaoxuefeng.com/wiki/1252599548343744/1304265949708322)

[Servlet 简介](https://www.runoob.com/servlet/servlet-intro.html)

---

## 什么是 Servlet

要编写一个完善的 HTTP 服务器，以 HTTP/1.1 为例，需要考虑的包括：

- 识别正确和错误的 HTTP 请求；
- 识别正确和错误的 HTTP 头；
- 复用 TCP 连接；
- 复用线程；
- IO 异常处理；
- ...

这些基础工作需要耗费大量的时间，并且经过长期测试才能稳定运行。如果我们只需要输出一个简单的 HTML 页面，就不得不编写上千行底层代码，那就根本无法做到高效而可靠地开发。

因此，在 JavaEE 平台上，处理 TCP 连接，解析 HTTP 协议这些底层工作统统扔给现成的 Web 服务器去做，我们只需要把自己的应用程序跑在 Web 服务器上。为了实现这一目的，JavaEE 提供了 Servlet API，我们使用 Servlet API 编写自己的 Servlet 来处理 HTTP 请求，Web 服务器实现 Servlet API 接口，实现底层功能。

&emsp;

### Servlet 示例

实现一个最简单的Servlet：

```java
// WebServlet注解表示这是一个Servlet，并映射到地址/:
@WebServlet(urlPatterns = "/")
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置响应类型:
        resp.setContentType("text/html");
        // 获取输出流:
        PrintWriter pw = resp.getWriter();
        // 写入响应:
        pw.write("<h1>Hello, world!</h1>");
        // 最后不要忘记flush强制输出:
        pw.flush();
    }
}
```

一个 `Servlet` 总是继承自 `HttpServlet`，然后覆写 `doGet()` 或 `doPost()` 方法。注意到`doGet()` 方法传入了 `HttpServletRequest` 和 `HttpServletResponse` 两个对象，分别代表 HTTP 请求和响应。我们使用 Servlet API 时，并不直接与底层 TCP 交互，也不需要解析HTTP 协议，因为 `HttpServletRequest` 和 `HttpServletResponse` 就已经封装好了请求和响应。以发送响应为例，我们只需要设置正确的响应类型，然后获取`PrintWriter`，写入响应即可。

在 maven 中引入依赖有两点需要注意：

* 这个`pom.xml`与前面我们讲到的普通 Java 程序有个区别，打包类型不是`jar`，而是`war`，表示 Java Web Application Archive：
  
  ```xml
  <packaging>war</packaging>
  ```

* 引入的 Servlet API 如下：
  
  ```xml
  <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>5.0.0</version>
      <scope>provided</scope>
  </dependency>
  ```

注意到`<scope>`指定为`provided`，表示编译时使用，但不会打包到`.war`文件中，因为运行期 Web 服务器本身已经提供了 Servlet API 相关的 jar 包。普通的 Java 程序是通过启动JVM，然后执行`main()`方法开始运行。但是 Web 应用程序有所不同，我们无法直接运行`war`文件，必须先启动 Web 服务器，再由 Web 服务器加载我们编写的`HelloServlet`，这样就可以让`HelloServlet`处理浏览器发送的请求。`Tomcat` 就是最常用的也是最广泛的开源免费的服务器。

&emsp;

### 什么是 Tomcat

Tomcat 因技术先进、性能稳定，开源免费，而深受 Java 开发者的喜爱并得到了部分软件开发商的认可，成为目前比较主流的 Web 应用服务器。Tomcat 简单的说就是一个运行 JAVA 的网络服务器，底层是 Socket 的一个程序，它也是 JSP 和 Servlet 的一个容器，可以看成是Apache 的扩展。

Servlet 容器是代替用户管理和调用 Servlet 的运行时外壳。那么什么是 Servlet 容器呢？ Servlet 容器，负责处理客户请求。当客户请求来到时，Servlet 容器获取请求，然后调用某个 Servlet，并把 Servlet 的执行结果返回给客户。当客户请求某个资源时，Servlet 容器使`SERVLETREQUEST` 对象把客户的请求信息封装起来，然后调用 JAVA Servlet API 中定义的Servlet 的一些生命周期方法，完成 Servlet 的执行，接着把 Servlet 执行的要返回给客户的结果封装到 `SERVLETRESPONSE` 对象中，最后SERVLET容器把客户的请求发送给客户，完成为客户的一次服务过程。

Tomcat 的一些关键目录包括：

- **/ bin** - Startup, shutdown和其他脚本。windows为`*.bat`文件，linux为 `*.sh`文件。
- **/ conf** - 配置文件和相关的DTDs。这里最重要的文件是 server.xml。它是容器的主要配置文件。
- **/ logs** - 日志文件默认位于此处。
- **/ webapps** - 这是您的 webApp 所在的位置。

&emsp;

## Servlet 的方法与继承关系

在程序中，所有自定义的 Servlet 均会去继承 `HttpServlet` 类，而 `HttpServlet` 继承的是 `GenericServlet` 这个抽象类，`GenericServlet` 又继承了 `Servlet` 这个接口。

`HttpServlet` -> `GenericServlet` -> `Servlet`

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

`service()` 方法在 `GenericServlet` 中并没有实现，具体的实现交给了 `HttpServlet`。在 `HttpServlet` 中，其实现方法就是对请求的方式做分析，到底是 `GET`， `POST` 还是其他请求，然后根据各自的请求调用相应的 `doXXX()` 方法。以 `doGet()` 为例，它只是会返回当前方法没有被支持，默认按照 405 的错误去报错，所以为什么在自定义的 `Servlet` 实现类中要根据自己需要的请求方式，自己实现相应的方法。

```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String protocol = req.getProtocol();
    String msg = lStrings.getString("http.method_get_not_supported");
    resp.sendError(this.getMethodNotSupportedCode(protocol), msg);
}
```

&emsp;

## Servlet 的生命周期

在上面介绍 `Servlet` 接口的定义方法里面，有两个与生命周期有关的方法，`init()` 和 `destroy()`，对应新建与销毁。

默认情况下，`Servlet` 在第一次接收到请求的时候才创建对象，进行实例化（调用构造方法）和初始化（调用 `init()`），创建对象后，所有的 URL 地址匹配的请求都由这同一个对象来处理，后面的每一次请求都会去调用 `service()` 方法。当容器关闭时，会调用 `destroy()`来销毁。在 Tomcat 中，每一个请求会被分配一个线程来处理，所以可以说，`Servlet` 是**单实例，多线程方式运行**的。既然 `Servlet` 是多线程方式运行，所以有线程安全方面的可能性，所以不能在处理请求的方法中修改成员变量的值。

如果希望在初始化的时候做一些准备工作，可以重写 `init()` 。

&emsp;

## ServletContext

`ServletContext` 直译的话叫做“ Servlet 上下文”，听着挺别扭。它其实就是个大容器，是个map。服务器会为每个应用创建一个 `ServletContext` 对象：

- `ServletContext` 对象的创建是在服务器启动时完成的
- `ServletContext` 对象的销毁是在服务器关闭时完成的

`ServletContext` 对象的作用是在整个Web应用的动态资源（Servlet/JSP）之间共享数据。例如在 A Servlet 中向 `ServletContext` 对象保存一个值，然后在 B Servlet 中就可以获取这个值。

这种用来装载共享数据的对象，在 Java Web 中共有4个，而且更习惯被成为“域对象”：

- ServletContext 域（Servlet 间共享数据）
- Session 域（一次会话间共享数据，也可以理解为多次请求间共享数据）
- Request 域（同一次请求共享数据）
- Page 域（JSP 页面内共享数据）

它们都可以看做是 map，都有 `getAttribute()` 和 `setAttribute()` 方法。

&emsp;

## 请求转发与重定向

发一个请求给 `Servlet`，接力棒就传递到了 `Servlet` 手中。而绝大部分情况下，`Servlet` 不能独自完成一切，需要把接力棒继续传递下去，此时我们就需要请求的转发或重定向。

&emsp;

### 转发

完整定义：在请求的处理过程中，`Servlet` 完成了自己的任务，需要把请求转交给下一个资源继续处理。**由于转发操作的核心部分是在服务器端完成的，所以浏览器感知不到，整个过程中浏览器只发送一次请求**。

```java
request.getRequestDispatcher("/apple.html").forward(request, response);
```

&emsp;

### 重定向

完整定义：在请求的处理过程中，`Servlet` 完成了自己的任务，然后以一个响应的方式告诉浏览器，要完成这个任务还需要你另外再访问下一个资源。**由于重定向操作的核心部分是在浏览器端完成的，所以整个过程中浏览器共发送两次请求**。

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

## 会话控制

在 Web 应用程序中，我们经常要跟踪用户身份。当一个用户登录成功后，如果他继续访问其他页面，Web 程序如何才能识别出该用户身份？

因为 HTTP 协议是一个无状态协议，即 Web 应用程序无法区分收到的两个 HTTP 请求是否是同一个浏览器发出的。为了跟踪用户状态，服务器可以向浏览器分配一个唯一 ID，并以Cookie 的形式发送到浏览器，浏览器在后续访问时总是附带此 Cookie，这样，服务器就可以识别用户身份。

&emsp;

### Session 的工作机制

浏览器正常访问服务器：

- 服务器端没调用 `request.getSession()` 方法：什么都不会发生
- 服务器端调用了 `request.getSession()` 方法，服务器端检查当前请求中是否携带了`JSESSIONID` 的 Cookie
  - 有：根据 `JSESSIONID` 在服务器端查找对应的 `HttpSession` 对象
    - 能找到：将找到的 `HttpSession` 对象作为 `request.getSession()` 方法的返回值返回
    - 找不到：服务器端新建一个 `HttpSession` 对象作为 `request.getSession()` 方法的返回值返回
  - 无：服务器端新建一个`HttpSession`对象作为 `request.getSession()` 方法的返回值返回

常用的 API 包括：

```java
session.getId()   // 获取 sessionId
session.isNew()   // 判断当前 session 是否是新的
session.getMaxInactiveInterval()  // session 的非激活间隔时长，默认1800秒
```

Session 保存作用域是和具体的某一个 session 对应的，常用的 API 有：

```java
void session.setAttribute(k, v)
Object session.getAttribute(k)
void removeAttribute(k)
```

&emsp;

## 开发技巧

### 利用反射

假设在一种情景下我们需要根据 URL 中包含的关键字去调用相对应的方法，传统的做法是获得关键词之后，通过 `if-else` 判断和字符串比较的方法找到与关键字相同的方法名，然后调用。这么做的弊端就是当新增方法后，`if-else` 会变得非常长，完全不利于维护。比较好的做法是通过反射，获取当前运行时类的所有方法，然后利用 `invoke()` 调用，这样无论后面新增多少方法这一部分都不需要更改。

&emsp;

### 中央控制器的使用

中央控制器的作用是根据业务请求，选择合适的 Controller 来处理，业务请求可以从请求体中查找。这里采用的做法是把所有的 Controller 都放在配置文件中，这样的好处是以后如果也许场景有增加，需要新增一个 Controller，只需在配置文件中配置即可生效。在中央控制器中，通过读取配置文件和反射，我们造出所有的对应 Controller 运行时对象，然后放在一个 Map 中。下面是一个 `DispatchServlet` 的示例：

```java
@WebServlet(name = "dispatchServlet", value = "/")
public class DispatchServlet extends HttpServlet {
    private final Map<String, Object> beanMap = new HashMap<>();

    public DispatchServlet() {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("applicationContext.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            NodeList beanList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanList.getLength(); i++) {
                Node beanNode = beanList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String id = beanElement.getAttribute("id");
                    String className = beanElement.getAttribute("class");

                    Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
                    beanMap.put(id, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        // 获取 Servlet 地址 eg. /fruit
        String servletPath = request.getServletPath();
        servletPath = servletPath.substring(1);

        Object controller = beanMap.get(servletPath);
        String operate = request.getParameter("operate");

        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method m : methods) {
            String name = m.getName();
            if (operate.equals(name)) {
                try {
                    m.invoke(controller);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
```

配置文件 `applicationContext.xml` 如下：

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>

<beans>
    <bean id="fruit" class="com.example.javaweb.controllers.FruitController"/>
</beans>
```

需要注意在 Maven 的文件结构下需要把它放在 `resources` 中。

这样做的好处是，如果是通过之前 new 的方法在 Servlet 中新建一个 `FruitController`，无论是写在某一个方法里面还是类属性里面，它的作用域，即生命周期都是有限的。现在我们在配置文件中定义了这个 `FruitController`，然后通过解析 XML，产生运行时类，存放在 `beanMap` 中。这种做法转移了各种实例的生命周期，控制权从程序员转移到了其他地方，这个现象称为控制反转。
