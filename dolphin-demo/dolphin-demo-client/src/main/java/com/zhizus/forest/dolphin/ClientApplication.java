package com.zhizus.forest.dolphin;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.zhizus.forest.dolphin.annotation.Inject;
import com.zhizus.forest.dolphin.gen.Sample;
import com.zhizus.forest.dolphin.hystrix.SampleClientCommand;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@SpringBootApplication
@EnableHystrix
@EnableCircuitBreaker
public class ClientApplication implements CommandLineRunner {

    @Inject("sampleClient")
    Sample.Client client;

    @Autowired
    SampleClientCommand sampleClientCommand;

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.run(args);

    }
//
//    @Bean
//    public HystrixCommandAspect hystrixCommandAspect() {
//        HystrixCommandAspect hystrixCommandAspect = new HystrixCommandAspect();
//        System.out.println(">>>>>>>>>>>>"+hystrixCommandAspect);
//        return hystrixCommandAspect;
//    }

    @Override
    public void run(String... args) throws Exception {
        String test = client.hello("test");
        System.out.println(test);

        String command = sampleClientCommand.hello("command");
        System.out.println(command);
    }
}
