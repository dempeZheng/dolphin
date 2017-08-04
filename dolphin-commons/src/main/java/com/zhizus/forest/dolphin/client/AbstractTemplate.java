package com.zhizus.forest.dolphin.client;

import org.apache.thrift.TServiceClient;

/**
 * Created by dempezheng on 2017/8/4.
 */
public abstract class AbstractTemplate<T extends TServiceClient> implements ClientTemplate<T> {

    private String serviceName;
    public ThriftClientFactory<T> iClient;

    private Class<T> type;

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }


    public AbstractTemplate(String serviceName) {
        this.serviceName = serviceName;

    }

    public AbstractTemplate(ThriftClientFactory<T> iClient) {
        this.iClient = iClient;
    }


    @Override
    public T newClient() throws Exception {
        return newClient(getType());
    }

    @Override
    public T newClient(Class<T> aClass) throws Exception {
        return this.iClient.iface(aClass, serviceName);
    }
}
