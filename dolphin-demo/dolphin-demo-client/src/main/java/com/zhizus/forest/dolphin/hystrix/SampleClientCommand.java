package com.zhizus.forest.dolphin.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zhizus.forest.dolphin.annotation.Inject;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;

/**
 * Created by dempezheng on 2017/8/3.
 */
@Service
public class SampleClientCommand implements Sample.Iface{

//    @LoadBalanced
    @Inject("sampleClient")
    Sample.Client client;


    @HystrixCommand(fallbackMethod = "helloFallback")
    public String hello(String para) throws TException {
//        throw new TException("test");
        client=null;
        Sample.Client client = new Sample.Client(null);
        return client.hello(para);



    }

    public boolean ping() throws TException {
        return client.ping();
    }

    public String helloFallback(String msg) {
        return "fallback_hello";
    }
}
