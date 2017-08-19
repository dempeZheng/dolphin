package com.zhizus.forest.dolphin;


import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.demo.ServerApplication;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServerApplication.class)
@WebAppConfiguration
public class HttpSampleTest {

    @THttpInject(value = "tHttpClient", serverArr = {"localhost:9090"}, path = "/sample",serviceName = "dolphin-server")
    Sample.Client tHttpClient;

    @Autowired
    Sample.Client client;

    @Test
    public void test2() throws TException {
        String hello = tHttpClient.hello("hello");
        System.out.println(hello);
        String hello2 = tHttpClient.hello("hello");
        String hello3 = tHttpClient.hello("hello");

        String test = client.hello("test");
        System.out.println(test);

    }


}
