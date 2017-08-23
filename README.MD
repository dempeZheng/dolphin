# dolphin
>基于spring boot支持thrift序列化的http的微服务框架

## future
- 支持thrift序列化的http协议
- 支持eureka的服务注册发现
- 基于spring boot


## quick start
### client
1.springboot启动时加上支持`EnableTHttpInject`注解
```java
@EnableTHttpInject
```
2.启用`EnableTHttpInject`之后，就可以这样使用http的thriftclient

```java
   @THttpInject(backupServers = {"localhost:9090"}, path = "/sample", serviceName = "dolphin-server")
    Sample.Client tHttpClient;

```

3.example
```java
@EnableTHttpInject
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ClientApplication implements CommandLineRunner {

    @THttpInject(backupServers = {"localhost:9090"}, path = "/sample", serviceName = "dolphin-server")
    Sample.Client tHttpClient;

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(tHttpClient.hello("test"));
    }

}
```

### server
加上`THttpService`注解，并实现对应的Iface接口
框架会自动根据`THttpService`的value值为name注册对应的servlet

```java
@THttpService("/sample")
public class SampleController implements Sample.Iface {

    @HystrixCommand(fallbackMethod = "helloFallback")
    @Override
    public String hello(String para) throws TException {
        throw new NullPointerException("");
//        return "hello+"+para;
    }

    @Override
    public boolean ping() throws TException {
        return true;
    }
    public String helloFallback(String para) {
        return "hello_fallback server";
    }
}

```

## 
### client线程安全问题
注入的client实际上是个代理类，真正的处理类已经通过threadlocal解决thrift client多线程的问题

## todo
- 支持spring xml配置
- 支持不使用eureka的情况
