# DispatcherServlet 基础实现

这个项目利用 Servlet 的基本知识，基于 MVC 和 IoC 的思想，实现一个 `DispatcherServlet`。它的基本思路包括：

* 通过配置文件读取项目中所有的 Controller ，纳入容器的 Bean 管理，这个过程是对 IoC 思想的实现。

* 通过访问路径来确定需要由哪一个 Controller 来处理相应的请求，通过解析出来的请求路径加上 Controller 名字得到类的名字，即配置文件中的 id。例如 `/user` 对应 `userController`。

* 根据该请求的方式 (GET, POST...) 来对应类中的实现方法。由于这里是一个简单实现，所以所有方法的参数均相同。

下面是核心代码：

```java
@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
    private final Map<String,Object> beanMap = new HashMap<>();

    public DispatcherServlet() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(this.getClass().getClassLoader().getResourceAsStream("applicationContext.xml"));
        // 获取所有的 bean 标签
        List<Node> beanNodes = document.selectNodes("//bean");
        // 遍历集合
        beanNodes.forEach(beanNode -> {
            Element beanElement = (Element) beanNode;
            // 获取id
            String id = beanElement.attributeValue("id");
            // 获取 className
            String className = beanElement.attributeValue("class");
            try {
                // 通过反射机制创建对象
                Class<?> clazz = Class.forName(className);
                Object bean = clazz.getDeclaredConstructor().newInstance();
                // 存储到 Map 集合
                beanMap.put(id, bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        /**
         * 基本思路：
         *      1. 获取 Servlet 对应路径
         *      2. 根据路径找到对应的 Controller  e.g  user -> UserController
         *      3. 调用相应 Controller 的方法
         */
        String servletPath = request.getServletPath();
        servletPath = servletPath.substring(1);
        System.out.println(servletPath);
        String controllerName = servletPath + "Controller";
        Object controller = beanMap.get(controllerName);

        String operate = request.getMethod().toLowerCase();
        // 找到对应方法
        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            System.out.println(methodName);
            if (methodName.equals(operate)) {
                try {
                    m.invoke(controller, request, response);
                    return;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("No corresponding method found.");
    }
}
```

这样设计的好处是通过一个中央控制器实现基本的请求转发，后期根据需要可在这里面进一步实现参数的提取，同时新增 Controller 也仅需通过新增配置文件内容实现。
