package com.zhizus.forest.dolphin.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.http4.NFHttpClient;
import com.netflix.http4.NFHttpClientFactory;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.client.thttp.DelegateLoadBalanceClient;
import com.zhizus.forest.dolphin.client.thttp.THttpClient;
import com.zhizus.forest.dolphin.exception.DolphinFrameException;
import com.zhizus.forest.dolphin.utils.ThriftClientUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dempezheng on 2017/8/17.
 */
public class ThreadLocalTHttpClient {

    private static ThreadLocal<Map<Object, Object>> thriftClientthreadLocal = new ThreadLocal<Map<Object, Object>>();

    public static Object getOrMakeClient(Field field, THttpInject annotation, Object object, SpringClientFactory springClientFactory) throws NoSuchMethodException, TTransportException, DolphinFrameException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<Object, Object> map = thriftClientthreadLocal.get();

        if (map == null) {
            map = Maps.newHashMap();
            thriftClientthreadLocal.set(map);
        }
        Object client = map.get(object);
        if (client == null) {
            client = newClient(field, annotation, springClientFactory);
            map.putIfAbsent(object, client);
        }
        return client;
    }


    public static Object newClient(Field field, THttpInject annotation, SpringClientFactory springClientFactory) throws NoSuchMethodException, TTransportException, DolphinFrameException, IllegalAccessException, InvocationTargetException, InstantiationException {
        TBinaryProtocol tBinaryProtocol = makeProtocol(annotation, springClientFactory);
        Class[] parameterTypes = {org.apache.thrift.protocol.TProtocol.class};
        Constructor constructor = field.getType().getConstructor(parameterTypes);
        Object client = constructor.newInstance(tBinaryProtocol);
        return client;

    }

    public static Object newProxyClient(Field field, THttpInject annotation, SpringClientFactory springClientFactory) throws NoSuchMethodException, DolphinFrameException, TTransportException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object client = newClient(field, annotation, springClientFactory);
        return getProxyBean(field, annotation, client, springClientFactory);
    }

    public static TBinaryProtocol makeProtocol(THttpInject annotation, SpringClientFactory springClientFactory) throws NoSuchMethodException, DolphinFrameException, TTransportException {
        String path = annotation.path();
        String[] serverArr = annotation.backupServers();
        if (serverArr.length < 1) {
            throw new DolphinFrameException();
        }
        List<String> backupServers = Lists.newArrayList();
        for (String s : serverArr) {
            String url = "http://" + s + path;
            backupServers.add(url);
        }
        NFHttpClient defaultClient = NFHttpClientFactory.getDefaultClient();
        String serviceId = annotation.serviceName();
        DelegateLoadBalanceClient delegateLoadBalanceClient = new DelegateLoadBalanceClient(springClientFactory, defaultClient, serviceId, path, backupServers);
        THttpClient trans = new THttpClient(delegateLoadBalanceClient);
        return new TBinaryProtocol(trans);

    }

    public static Object getProxyBean(Field field, THttpInject annotation, Object bean, SpringClientFactory springClientFactory) {
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
                Object orMakeClient = getOrMakeClient(field, annotation, bean, springClientFactory);
                Object object = invocation.getMethod().invoke(orMakeClient, invocation.getArguments());
                return object;

            }
        });
        return proxyFactory.getObject();
    }
}
