package com.zhizus.forest.dolphin.client;

import com.google.common.collect.Lists;
import com.netflix.http4.NFHttpClient;
import com.netflix.http4.NFHttpClientFactory;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by dempezheng on 2017/8/22.
 */
public class TServiceClientFactory{

    private SpringClientFactory factory;

    public TServiceClientFactory(SpringClientFactory factory) {
        this.factory = factory;
    }

    public Object newClient(Field field, THttpInject annotation) throws TTransportException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        TBinaryProtocol tBinaryProtocol = makeProtocol(annotation);
        Class[] parameterTypes = {org.apache.thrift.protocol.TProtocol.class};
        Constructor constructor = field.getType().getConstructor(parameterTypes);
        Object client = constructor.newInstance(tBinaryProtocol);
        return client;
    }


    private TBinaryProtocol makeProtocol(THttpInject annotation) throws TTransportException {
        String path = annotation.path();
        String[] serverArr = annotation.backupServers();
        List<String> backupServers = Lists.newArrayList();
        if (serverArr.length > 0) {
            for (String s : serverArr) {
                String url = "http://" + s + path;
                backupServers.add(url);
            }
        }
        NFHttpClient defaultClient = NFHttpClientFactory.getDefaultClient();
        String serviceId = annotation.serviceName();
        LoadBalanceDelegateClient loadBalanceDelegateClient = new LoadBalanceDelegateClient(factory, defaultClient, serviceId, path, backupServers);
        THttpClient trans = new THttpClient(loadBalanceDelegateClient);
        return new TBinaryProtocol(trans);

    }


}
