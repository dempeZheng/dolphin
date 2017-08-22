# dolphin
>基于spring boot的微服务框架



## future
- 支持thrift序列化的http协议
- 支持ribbon的ThriftTemplate
- 支持eureka的服务注册发现
- 基于spring boot


## quick start
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

## 
### client线程安全安全问题
注入的client实际上是个代理类，真正的处理类已经通过threadlocal解决thrift client多线程的问题

## todo
- 支持eureka的各种重试
- 改造支持interceptor模式，支持负载均衡的client和非负载均衡的client
- 支持fegin
- 优化配置
- RxJava封装异步Thrift Client
- 支持spring xml配置
