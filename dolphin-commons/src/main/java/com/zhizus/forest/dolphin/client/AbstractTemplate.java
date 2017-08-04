package com.zhizus.forest.dolphin.client;

import com.netflix.client.ClientFactory;
import com.netflix.client.IClient;
import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpRequest;
import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpResponse;

/**
 * Created by dempezheng on 2017/8/4.
 */
public abstract class AbstractTemplate<T> implements ClientTemplate<T> {

    private IClient<THttpRequest<T>, THttpResponse> iClient;

    public AbstractTemplate(String name) {
        this.iClient = ClientFactory.getNamedClient(name);
    }

    @Override
    public T newClient() throws Exception {
        return (T) iClient.execute(null, null).getPayload();
    }
}
