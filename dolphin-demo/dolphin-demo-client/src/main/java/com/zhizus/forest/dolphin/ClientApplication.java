package com.zhizus.forest.dolphin;

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

    @Override
    public void run(String... args) throws Exception {
        runHello();
        runHystrixCommandHello();
    }

    private void runHello() throws TException {
        String test = client.hello("test");
        System.out.println(test);

    }
    private void runHystrixCommandHello() throws TException {
        String command = sampleClientCommand.hello("command");
        System.out.println(command);
    }
}
