package com.zhizus.forest.dolphin.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class HThriftLoadBalanceRequest<T> implements LoadBalancerRequest<T> {


    public HThriftLoadBalanceRequest(){

    }
    @Override
    public T apply(ServiceInstance instance) throws Exception {

        // serverinfo
        //create thrift client




        return null;
    }
}
