package com.zhizus.forest.dolphin.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zhizus.forest.dolphin.annotation.ThriftMethodProvider;
import com.zhizus.forest.dolphin.annotation.ThriftService;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@ThriftService("/sample")
public class SampleController implements Sample.Iface {

    @HystrixCommand(fallbackMethod = "helloFallback")
    @ThriftMethodProvider
    @Override
    public String hello(String para) throws TException {
        throw new NullPointerException("");
//        return "hello+"+para;
    }

    @ThriftMethodProvider
    @Override
    public boolean ping() throws TException {
        return true;
    }

    public String helloFallback(String para){
        return "hello_fallback server";
    }
}
