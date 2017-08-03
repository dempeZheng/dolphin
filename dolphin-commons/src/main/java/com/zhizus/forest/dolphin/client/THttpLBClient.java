package com.zhizus.forest.dolphin.client;

import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.ClientRequest;
import com.netflix.client.IResponse;
import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpLBClient extends AbstractLoadBalancerAwareClient {

    public THttpLBClient(ILoadBalancer lb) {
        super(lb);
    }

    public THttpLBClient(ILoadBalancer lb, IClientConfig clientConfig) {
        super(lb, clientConfig);
    }

    @Override
    public RequestSpecificRetryHandler getRequestSpecificRetryHandler(ClientRequest request, IClientConfig requestConfig) {
        return null;
    }

    @Override
    public IResponse execute(ClientRequest request, IClientConfig requestConfig) throws Exception {
        return null;
    }
}
