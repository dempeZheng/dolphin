package com.zhizus.forest.dolphin.client.ribbon.thrift;

import com.zhizus.forest.dolphin.client.AbstractTemplate;
import org.apache.thrift.TServiceClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * Created by dempezheng on 2017/8/4.
 */
public class ThriftTemplate<T extends TServiceClient> extends AbstractTemplate<T> {

    public ThriftTemplate(String name, Class<T> classType, SpringClientFactory clientFactory) {
        super(name, classType);
        this.iClient = new ThriftClientFactory<>(clientFactory);
    }

}
