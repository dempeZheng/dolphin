package com.zhizus.forest.dolphin.client;

import com.google.common.collect.Maps;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.utils.ThriftClientUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.thrift.TServiceClient;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * 代理模式解决单例模式下thrift client线程安全问题
 * Created by dempezheng on 2017/8/17.
 */
public class TServiceProxyClientFactory extends TServiceClientFactory {

    private static ThreadLocal<Map<Object, Object>> thriftClientThreadLocal = new ThreadLocal<Map<Object, Object>>();

    public TServiceProxyClientFactory(SpringClientFactory factory, THttpDelegate defaultClient) {
        super(factory, defaultClient);
    }

    private Object getClient(TServiceBuilder builder, TServiceClient object) throws Exception {
        Map<Object, Object> map = thriftClientThreadLocal.get();
        if (map == null) {
            map = Maps.newHashMap();
            thriftClientThreadLocal.set(map);
        }
        Object client = map.get(object);
        if (client == null) {
            client = applyProxyClient(builder, object.getClass());
            map.putIfAbsent(object, client);
        }
        return client;
    }

    private Object getProxyBean(TServiceBuilder builder, TServiceClient bean) {
        ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
        proxyFactory.setTarget(bean);
        proxyFactory.setProxyTargetClass(true);// ProxyFactoryBean要代理的不是接口类，
        // 而是要使用CGLIB方式来进行代理,jdk代理类型对于thrift.client会有类型转换的问题
        proxyFactory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                String methodName = invocation.getMethod().getName();
                Set<String> interfaceMethodNames = ThriftClientUtils.getInterfaceMethodNames(bean.getClass());
                if (interfaceMethodNames.contains(methodName)) {
                    return invocation.proceed();
                }
                Object orMakeClient = getClient(builder, bean);
                Object object = invocation.getMethod().invoke(orMakeClient, invocation.getArguments());
                return object;
            }
        });
        return proxyFactory.getObject();
    }

    public Object applyProxyClient(Field field, THttpInject annotation) throws Exception {
        TServiceBuilder tServiceBuilder = new TServiceBuilder().withPath(annotation.path())
                .withServiceId(annotation.serviceName())
                .withBackupOfServerList(annotation.backupServers());
        TServiceClient client = applyClient(tServiceBuilder, (Class<? extends TServiceClient>) field.getType());
        return getProxyBean(tServiceBuilder, client);
    }

    public <T extends TServiceClient> T applyProxyClient(TServiceBuilder builder, Class<T> clazz) throws Exception {
        TServiceClient client = applyClient(builder, clazz);
        return (T) getProxyBean(builder, client);
    }

}
