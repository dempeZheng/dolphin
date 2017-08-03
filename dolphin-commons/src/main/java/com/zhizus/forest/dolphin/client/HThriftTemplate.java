package com.zhizus.forest.dolphin.client;

import org.apache.thrift.TServiceClient;
import org.springframework.web.client.RestClientException;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class HThriftTemplate<T, R, C extends TServiceClient> {

    private C client;

    public HThriftTemplate(C client) {
        this.client = client;
    }

    public C getClient() {
        return client;
    }

    public <T> T execute(C request) throws RestClientException {

        return null;
    }


}
