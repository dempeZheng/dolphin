#lamp
>基于netty&spring容器封装的json通讯协议组件

##modules
![Alt text](./doc/lamp-modules.png)
##protocol
>自定义协议

Request
``` java
	protected int messageID;//消息序列号id，用于标识消息

    private String uri;// 消息uri，用于路由消息到指定controller method

    private Map<String, String> paramMap;// 请求参数map，支持基本类型参数
```


Response
``` java
 private int messageID;// 对应请求消息的id

 private String data;// 消息返回数据，通常为json串
```


##example

###服务端的打开方式

####基于spring注解的打开方式

``` java
@Configuration
@ComponentScan
public class LampServer {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(LampServer.class);
        BaseServer baseServer = new BaseServer(DefConfigFactory.createDEVConfig(), context);
        baseServer.start();
    }
}
```
####基于spring xml的打开方式

``` java
 ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application.xml"});
        BaseServer baseServer = new BaseServer(DefConfigFactory.createDEVConfig(), context);
        baseServer.start();
```

application.xml配置如下

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <!-- 主动搜索以下目录 -->
    <context:component-scan base-package="com.dempe.lamp"/>

</beans>


```

###业务逻辑的编写方式

``` java

@Controller("sample")
public class SampleController {

    //依赖spring的注入
    @Resource
    SampleService lampService;

    /**
     * 默认匹配path getUri=/sample/hello
     * 默认注入request name属性的参数值
     * @return
     */
    @Path
    public JSONObject hello(@Param String name,@Param int age) {
//        System.out.println(age);
        return lampService.hello(name);
    }
}

```


###客户端的使用方式

``` java
/**
 * 简单client示例
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class SampleClient {

    public static void main(String[] args) throws Exception {
        futureClientExample();
    }

    /**
     * FutureClient example
     * @throws Exception
     */
    public static void futureClientExample() throws Exception {
        FutureClient client = new FutureClient("localhost", 8888);
        // 构造json请求协议
        Request request = buildRequest();
        Future<Response> future = client.send(request);
        System.out.println(future.await());
    }

    /**
     * BlockingClient example
     * @throws Exception
     */
    public static void blockingClientExample() throws Exception {
        BlockingClient client = new BlockingClient("localhost", 8888);
        Request request = buildRequest();
        Response response = client.send(request);
        System.out.println(response);
    }

    /**
     * CallbackClient example
     * @throws Exception
     */
    public static void callbackClientExample() throws Exception {
        CallbackClient client = new CallbackClient("localhost", 8888);
        Request request = buildRequest();
        client.send(request, new Callback() {
            @Override
            public void onReceive(Object message) {
                System.out.println(message);
            }
        });
    }

    public static Request buildRequest() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "dempe");
        data.put("age", "1");
        Request request = new Request();
        request.setUri("/sample/hello");
        request.setParamMap(data);
        return request;
    }


    /**
     * 压测方法
     * @throws Exception
     */
    public static void stressTesting() throws Exception {
        MetricThread thread = new MetricThread("client");
        List<FutureClient> clientList = new ArrayList<FutureClient>();
        int size = 8;
        for (int i = 0; i < size; i++) {
            clientList.add(new FutureClient("localhost", 8888));
        }
        int i = 0;
        while (true) {
            i++;
            thread.increment();
            FutureClient client = clientList.get(i % size);
            // 初始化client
            Request request = buildRequest();
            //发送请求并返回响应
            Future<Response> future = client.send(request);
            if (i % 100000 == 0) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }


}

```

###性能测试

>开发机 4核 8G win7  7w+
