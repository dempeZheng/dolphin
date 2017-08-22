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

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@EnableTHttpInject
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ClientApplication implements CommandLineRunner {

    @THttpInject(backupServers = {"localhost:9090"}, path = "/sample")
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
