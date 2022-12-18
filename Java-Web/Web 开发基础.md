# Web 开发基础

**参考文档**

[第六章 HTTP协议](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter06/)

---

## BS 架构

今天我们访问网站，使用 App 时，都是基于 Web 这种 Browser/Server 模式，简称 BS 架构，它的特点是，客户端只需要浏览器，应用程序的逻辑和数据都存储在服务器端。浏览器只需要请求服务器，获取 Web 页面，并把 Web 页面展示给用户即可。

Web 页面具有极强的交互性。由于 Web 页面是用 HTML 编写的，而 HTML 具备超强的表现力，并且，服务器端升级后，客户端无需任何部署就可以使用到新的版本，因此，BS 架构升级非常容易。

&emsp;

## HTTP 协议

在 Web 应用中，浏览器请求一个 URL，服务器就把生成的 HTML 网页发送给浏览器，而浏览器和服务器之间的传输协议是 HTTP，所以：

- HTML 是一种用来定义网页的文本，会 HTML，就可以编写网页；

- HTTP 是在网络上传输 HTML 的协议，用于浏览器和服务器的通信。

&emsp;

### 请求与响应

对于浏览器来说，请求页面的流程如下：

1. 与服务器建立 TCP 连接；
2. 发送 HTTP 请求；
3. 收取 HTTP 响应，然后把网页在浏览器中显示出来。

浏览器发送的 HTTP 请求如下：

```http
GET / HTTP/1.1
Host: www.sina.com.cn
User-Agent: Mozilla/5.0 xxx
Accept: */*Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8
```

其中，第一行表示使用`GET`请求获取路径为`/`的资源，并使用`HTTP/1.1`协议，从第二行开始，每行都是以`Header: Value`形式表示的 HTTP 头，比较常用的 HTTP Header 包括：

- Host: 表示请求的主机名，因为一个服务器上可能运行着多个网站，因此，Host表示浏览器正在请求的域名；
- User-Agent: 标识客户端本身，例如Chrome浏览器的标识类似`Mozilla/5.0 ... Chrome/79`，IE浏览器的标识类似`Mozilla/5.0 (Windows NT ...) like Gecko`；
- Accept：表示浏览器能接收的资源类型，如`text/*`，`image/*`或者`*/*`表示所有；
- Accept-Language：表示浏览器偏好的语言，服务器可以据此返回不同语言的网页；
- Accept-Encoding：表示浏览器可以支持的压缩类型，例如`gzip, deflate, br`。

服务器的响应如下：

```http
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 21932
Content-Encoding: gzip
Cache-Control: max-age=300

<html>...网页数据...
```

服务器响应的第一行总是版本号+空格+数字+空格+文本，数字表示响应代码，其中`2xx`表示成功，`3xx`表示重定向，`4xx`表示客户端引发的错误，`5xx`表示服务器端引发的错误。数字是给程序识别，文本则是给开发者调试使用的。常见的响应代码有：

- 200 OK：表示成功；
- 301 Moved Permanently：表示该URL已经永久重定向；
- 302 Found：表示该URL需要临时重定向；
- 304 Not Modified：表示该资源没有修改，客户端可以使用本地缓存的版本；
- 400 Bad Request：表示客户端发送了一个错误的请求，例如参数无效；
- 401 Unauthorized：表示客户端因为身份未验证而不允许访问该URL；
- 403 Forbidden：表示服务器因为权限问题拒绝了客户端的请求；
- 404 Not Found：表示客户端请求了一个不存在的资源；
- 500 Internal Server Error：表示服务器处理时内部出错，例如因为无法连接数据库；
- 503 Service Unavailable：表示服务器此刻暂时无法处理请求。

从第二行开始，服务器每一行均返回一个 HTTP 头。服务器经常返回的 HTTP Header 包括：

- Content-Type：表示该响应内容的类型，例如`text/html`，`image/jpeg`；
- Content-Length：表示该响应内容的长度（字节数）；
- Content-Encoding：表示该响应压缩算法，例如`gzip`；
- Cache-Control：指示客户端应如何缓存，例如`max-age=300`表示可以最多缓存300秒。
