package com.zhizus.forest.dolphin.client;

import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpClientFactory;
import org.apache.thrift.TServiceClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * Created by dempezheng on 2017/8/4.
 */
public class THttpTemplate<T extends TServiceClient> extends AbstractTemplate<T> {


    public THttpTemplate(String name, SpringClientFactory clientFactory) {
        super(name);
        this.iClient = new THttpClientFactory<T>(clientFactory);
    }
}
