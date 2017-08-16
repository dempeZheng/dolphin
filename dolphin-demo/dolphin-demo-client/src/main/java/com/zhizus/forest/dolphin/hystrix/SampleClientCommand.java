package com.zhizus.forest.dolphin.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpTemplate;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dempezheng on 2017/8/3.
 */
@Service
public class SampleClientCommand implements Sample.Iface {

    @Autowired
    THttpTemplate<Sample.Client> template;


    @HystrixCommand(fallbackMethod = "helloFallback")
    public String hello(String para) throws TException {
        Sample.Client tClient = null;
        try {
            tClient = template.newClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tClient.hello("test");
    }

    public boolean ping() throws TException {
        try {
            return template.newClient().ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String helloFallback(String msg) {
        return "fallback_hello";
    }
}
