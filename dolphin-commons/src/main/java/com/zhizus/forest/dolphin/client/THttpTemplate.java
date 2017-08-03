package com.zhizus.forest.dolphin.client;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpTemplate {

    public <X extends TServiceClient> X iface(Class<X> ifaceClass) throws Exception {
        final TTransport transport = null;
        // 代理
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(ifaceClass);
        factory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method m) {
                return true;
            }
        });
        try {
            X x = (X) factory.create(new Class[]{TProtocol.class}, new Object[]{new TBinaryProtocol(transport)});
            ((Proxy) x).setHandler(new MethodHandler() {
                @Override
                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    try {
                        Object result = proceed.invoke(self, args);
                        //统计调用次数

                        return result;
                    } finally {

                    }
                }
            });
            return x;
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException("fail to create proxy.", e);
        }
    }
}
