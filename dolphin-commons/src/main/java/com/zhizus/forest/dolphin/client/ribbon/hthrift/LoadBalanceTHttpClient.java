package com.zhizus.forest.dolphin.client.ribbon.hthrift;

import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
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
import java.lang.reflect.ParameterizedType;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class LoadBalanceTHttpClient<T extends TServiceClient> extends AbstractLoadBalancerAwareClient<THttpRequest<T>, THttpResponse> {

    public LoadBalanceTHttpClient(ILoadBalancer lb) {
        super(lb);
    }

    public LoadBalanceTHttpClient(ILoadBalancer lb, IClientConfig clientConfig) {
        super(lb, clientConfig);
    }

    @Override
    public RequestSpecificRetryHandler getRequestSpecificRetryHandler(THttpRequest<T> request, IClientConfig requestConfig) {
        return null;
    }

    @Override
    public THttpResponse execute(THttpRequest request, IClientConfig requestConfig) throws Exception {
        Class<T> entityClass = getGenericParadigmType();
        T iface = iface(entityClass);
        return new THttpResponse<>(iface);
    }


    public T iface(Class<T> ifaceClass) throws Exception {
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
            T t = (T) factory.create(new Class[]{TProtocol.class}, new Object[]{new TBinaryProtocol(transport)});
            ((Proxy) t).setHandler(new MethodHandler() {
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
            return t;
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException("fail to create proxy.", e);
        }
    }

    private Class<T> getGenericParadigmType() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }


}
