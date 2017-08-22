package com.zhizus.forest.dolphin;

import com.zhizus.forest.dolphin.annotation.EnableTHttpInject;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.eureka.RibbonEurekaAutoConfiguration;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@EnableTHttpInject
@SpringBootApplication(exclude = RibbonEurekaAutoConfiguration.class)
//@EnableDiscoveryClient
@EnableCircuitBreaker
//@RibbonClients
public class ClientApplication implements CommandLineRunner {

    @THttpInject( path = "/sample", serviceName = "dolphin-server2")
    Sample.Client tHttpClient;

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(tHttpClient.hello("test"));
        System.out.println(tHttpClient.hello("test"));
        System.out.println(tHttpClient.hello("test"));
        System.out.println(tHttpClient.hello("test"));
        System.out.println(tHttpClient.hello("test"));
        System.out.println(tHttpClient.hello("test"));
    }

}
