package com.zhizus.forest.dolphin.client.ribbon;

import com.google.common.collect.Lists;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.zhizus.forest.dolphin.client.TClientFactory;
import com.zhizus.forest.dolphin.utils.ThriftClientUtils;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.cloud.netflix.ribbon.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by dempezheng on 2017/8/4.
 */
public abstract class AbstractTClientFactory<T extends TServiceClient> implements TClientFactory<T> {

    private SpringClientFactory clientFactory;

    public AbstractTClientFactory(SpringClientFactory clientFactory) {
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
        final TTransport transport = makeTransport(server);

        // 代理
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(ifaceClass);
        factory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method m) {
                return ThriftClientUtils.getInterfaceMethodNames(ifaceClass).contains(m.getName());
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

                        transport.close();

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

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }


    public abstract TTransport makeTransport(Server server) throws TTransportException;

    protected Server getServer(ILoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            return null;
        }
        List<Server> listServer = Lists.newArrayList();
        Server server = new Server("localhost", 9001);
        listServer.add(server);
        loadBalancer.addServers(listServer);
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


