package com.zhizus.forest.dolphin.client.ribbon.hthrift;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.zhizus.forest.dolphin.client.ThriftClientFactory;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.springframework.cloud.netflix.ribbon.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpClientFactory<T extends TServiceClient> implements ThriftClientFactory<T> {

    private SpringClientFactory clientFactory;

    public THttpClientFactory(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public T iface(Class<T> ifaceClass, String serviceName) throws Exception {
        ILoadBalancer loadBalancer = getLoadBalancer(serviceName);
        Server server = getServer(loadBalancer);
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceName);
        }

        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceName);
        RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);
        final TTransport transport = new THttpClient("http://" + server.getHost() + ":" + server.getPort());
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
                        Object returnVal = proceed.invoke(self, args);
                        statsRecorder.recordStats(returnVal);
                        return returnVal;
                    } catch (Exception ex) {
                        statsRecorder.recordStats(ex);
                        ReflectionUtils.rethrowRuntimeException(ex);
                    } finally {

                    }
                    return null;
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

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }


    protected Server getServer(ILoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            return null;
        }
        return loadBalancer.chooseServer("default"); // TODO: better handling of key
    }

    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        if (config != null) {
            Boolean isSecure = config.get(CommonClientConfigKey.IsSecure);
            if (isSecure != null) {
                return isSecure;
            }
        }

        return serverIntrospector(serviceId).isSecure(server);
    }

    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }


}
