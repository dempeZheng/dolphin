package com.zhizus.forest.dolphin;

import org.apache.thrift.TException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ClientApplication implements CommandLineRunner {

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
    }

}
