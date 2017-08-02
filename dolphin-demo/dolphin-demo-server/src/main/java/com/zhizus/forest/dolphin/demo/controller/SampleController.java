package com.zhizus.forest.dolphin.demo.controller;

import com.zhizus.forest.dolphin.annotation.ThriftService;
import com.zhizus.forest.dolphin.demo.Sample;
import org.apache.thrift.TException;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@ThriftService("/sample")
public class SampleController implements Sample.Iface {

    @Override
    public String hello(String para) throws TException {
        return "hello+"+para;
    }

    @Override
    public boolean ping() throws TException {
        return true;
    }
}
