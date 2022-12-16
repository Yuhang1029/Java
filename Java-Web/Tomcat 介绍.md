# Tomcat 介绍

**参考文档**

[第五章 Tomcat](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter05/)

[浅谈什么是 Tomcat](http://www.bjpowernode.com/hot/735.html)

---

## 什么是 Tomcat

Tomcat 因技术先进、性能稳定，开源免费，而深受 Java 开发者的喜爱并得到了部分软件开发商的认可，成为目前比较主流的 Web 应用服务器。Tomcat 简单的说就是一个运行 JAVA 的网络服务器，底层是 Socket 的一个程序，它也是 JSP 和 Servlet 的一个容器，可以看成是Apache 的扩展。

Servlet 容器是代替用户管理和调用 Servlet 的运行时外壳。那么什么是 Servlet 容器呢？ Servlet 容器，负责处理客户请求。当客户请求来到时，Servlet 容器获取请求，然后调用某个 Servlet，并把 Servlet 的执行结果返回给客户。当客户请求某个资源时，Servlet 容器使`SERVLETREQUEST` 对象把客户的请求信息封装起来，然后调用 JAVA Servlet API 中定义的Servlet 的一些生命周期方法，完成 Servlet 的执行，接着把 Servlet 执行的要返回给客户的结果封装到 `SERVLETRESPONSE` 对象中，最后SERVLET容器把客户的请求发送给客户，完成为客户的一次服务过程。

Tomcat 的一些关键目录包括：

- **/ bin** - Startup, shutdown和其他脚本。windows为`*.bat`文件，linux为 `*.sh`文件。
- **/ conf** - 配置文件和相关的DTDs。这里最重要的文件是 server.xml。它是容器的主要配置文件。
- **/ logs** - 日志文件默认位于此处。
- **/ webapps** - 这是您的 webApp 所在的位置。


