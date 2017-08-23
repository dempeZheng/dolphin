package com.zhizus.forest.dolphin;

import com.zhizus.forest.dolphin.annotation.EnableTHttpInject;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.client.TServiceClientFactory;
import com.zhizus.forest.dolphin.client.TServiceProxyClientFactory;
import com.zhizus.forest.dolphin.exception.DolphinFrameException;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.eureka.RibbonEurekaAutoConfiguration;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@EnableTHttpInject
@SpringBootApplication(exclude = RibbonEurekaAutoConfiguration.class)
//@EnableDiscoveryClient
@EnableCircuitBreaker
//@RibbonClients
public class ClientApplication implements CommandLineRunner {

    @THttpInject(path = "/sample", serviceName = "dolphin-server2")
    Sample.Client tHttpClient;


    @Autowired
    SpringClientFactory factory;

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        helloByAnnotation();
//        hello();
        helloByProxyClient();

    }

    public void helloByAnnotation() throws Exception {
        System.out.println(tHttpClient.hello("test"));
    }

    /**
     *
     */
    public void hello() throws InvocationTargetException, NoSuchMethodException, TException, InstantiationException, IllegalAccessException {
        TServiceClientFactory tServiceClientFactory = new TServiceClientFactory(factory);
        TServiceClientFactory.TServiceBuilder builder = new TServiceClientFactory.TServiceBuilder()
                .withBackupOfServerList("localhost:9090")
                .withPath("/sample")
                .withServiceId("sample");
        Sample.Client client = tServiceClientFactory.applyClient(builder, Sample.Client.class);
        String sayHello = client.hello("sayHello");
        System.out.println(sayHello);
    }


    public void helloByProxyClient() throws Exception {
        TServiceProxyClientFactory tServiceClientFactory = new TServiceProxyClientFactory(factory);
        TServiceClientFactory.TServiceBuilder builder = new TServiceClientFactory.TServiceBuilder()
//                .withBackupOfServerList("localhost:9090")
                .withPath("/sample")
                .withServiceId("dolphin-server2");
        Sample.Client client = tServiceClientFactory.applyProxyClient(builder, Sample.Client.class);
        String sayHello = client.hello("sayHello");
        System.out.println(sayHello);
    }
}
