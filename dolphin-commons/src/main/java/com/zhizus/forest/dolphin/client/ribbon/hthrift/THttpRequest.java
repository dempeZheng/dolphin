package com.zhizus.forest.dolphin.client.ribbon.hthrift;

import com.netflix.client.ClientRequest;

import java.net.URI;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpRequest<T> extends ClientRequest {

    private Class<T> tClientType;

    public THttpRequest(URI uri, Class<T> tClientType) {
        super(uri);
        this.tClientType = tClientType;
    }

    public Class<T> gettClientType() {
        return tClientType;
    }

    public THttpRequest settClientType(Class<T> tClientType) {
        this.tClientType = tClientType;
        return this;
    }
}
