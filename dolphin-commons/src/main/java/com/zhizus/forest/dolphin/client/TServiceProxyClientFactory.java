package com.zhizus.forest.dolphin.client;

import com.google.common.collect.Maps;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.exception.DolphinFrameException;
import com.zhizus.forest.dolphin.utils.ThriftClientUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.thrift.transport.TTransportException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * 代理模式解决单例模式下thrift client线程安全问题
 * Created by dempezheng on 2017/8/17.
 */
public class TServiceProxyClientFactory extends TServiceClientFactory {

    private static ThreadLocal<Map<Object, Object>> thriftClientThreadLocal = new ThreadLocal<Map<Object, Object>>();

    public TServiceProxyClientFactory(SpringClientFactory factory) {
        super(factory);
    }

    private Object getClient(Field field, THttpInject annotation, Object object) throws NoSuchMethodException, TTransportException, DolphinFrameException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<Object, Object> map = thriftClientThreadLocal.get();
        if (map == null) {
            map = Maps.newHashMap();
            thriftClientThreadLocal.set(map);
        }
        Object client = map.get(object);
        if (client == null) {
            client = newClient(field, annotation);
            map.putIfAbsent(object, client);
        }
        return client;
    }

    private Object getProxyBean(Field field, THttpInject annotation, Object bean) {
        ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
        proxyFactory.setTarget(bean);
        proxyFactory.setProxyTargetClass(true);// ProxyFactoryBean要代理的不是接口类，
        // 而是要使用CGLIB方式来进行代理,jdk代理类型对于thrift.client会有类型转换的问题
        proxyFactory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                String methodName = invocation.getMethod().getName();
                Set<String> interfaceMethodNames = ThriftClientUtils.getInterfaceMethodNames(field.getType());
                if (interfaceMethodNames.contains(methodName)) {
                    return invocation.proceed();
                }
                Object orMakeClient = getClient(field, annotation, bean);
                Object object = invocation.getMethod().invoke(orMakeClient, invocation.getArguments());
                return object;
            }
        });
        return proxyFactory.getObject();
    }

    public Object applyProxyClient(Field field, THttpInject annotation) throws NoSuchMethodException, TTransportException, InstantiationException, DolphinFrameException, IllegalAccessException, InvocationTargetException {
        Object client = newClient(field, annotation);
        return getProxyBean(field, annotation, client);
    }
}
