package com.zhizus.forest.dolphin.demo;

import com.zhizus.forest.dolphin.annotation.EnableTHttpInject;
import com.zhizus.forest.dolphin.configuration.ThriftAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@EnableTHttpInject
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication(exclude = ThriftAutoConfiguration.class)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
