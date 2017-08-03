package com.zhizus.forest.dolphin;


import com.zhizus.forest.dolphin.annotation.Inject;
import com.zhizus.forest.dolphin.demo.Application;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class HttpSampleTest {


    @Inject
    Sample.Client client;

    @Test
    public void test2() throws TException {
        String hello = client.hello("hello");
        System.out.println(hello);
    }


}
