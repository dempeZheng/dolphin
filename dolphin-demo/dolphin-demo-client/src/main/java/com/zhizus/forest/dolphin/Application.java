package com.zhizus.forest.dolphin;

import com.zhizus.forest.dolphin.annotation.Inject;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@SpringBootApplication

public class Application implements CommandLineRunner {

    @Inject("sampleClient")
    Sample.Client client;

    public static void main(String[] args) throws TException {
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebEnvironment(false);
        app.run(args);

    }

    @Override
    public void run(String... args) throws Exception {
        String test = client.hello("test");
        System.out.println(test);
    }
}
