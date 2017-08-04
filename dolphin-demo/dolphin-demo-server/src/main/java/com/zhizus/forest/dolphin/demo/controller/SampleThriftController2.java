package com.zhizus.forest.dolphin.demo.controller;

import com.zhizus.forest.dolphin.annotation.ThriftMethodProvider;
import com.zhizus.forest.dolphin.annotation.ThriftService;
import com.zhizus.forest.dolphin.gen.Sample;
import com.zhizus.forest.dolphin.server.ProcessorFactory;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;

/**
 * Created by dempezheng on 2017/8/4.
 */
@ThriftService(value = "thriftSample2", port = 9002)
public class SampleThriftController2 implements ProcessorFactory, Sample.Iface {
    @ThriftMethodProvider
    @Override
    public String hello(String para) throws TException {
        return "hello+" + para;
    }

    @ThriftMethodProvider
    @Override
    public boolean ping() throws TException {
        return true;
    }

    @Override
    public TProcessor getProcessor(Object bean) {
        return new Sample.Processor<SampleThriftController2>(this);
    }
}
