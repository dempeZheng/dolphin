package com.zhizus.forest.dolphin.client.ribbon.hthrift;

import com.zhizus.forest.dolphin.client.AbstractTemplate;
import org.apache.thrift.TServiceClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * Created by dempezheng on 2017/8/4.
 */
public class THttpTemplate<T extends TServiceClient> extends AbstractTemplate<T> {

    public THttpTemplate(String name, String urlPath, Class<T> classType, SpringClientFactory clientFactory) {
        super(name, classType);
        this.iClient = new THttpClientFactory<T>(urlPath,clientFactory);
    }
}
