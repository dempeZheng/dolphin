package com.zhizus.forest.dolphin.client.ribbon.thrift;

import com.netflix.loadbalancer.Server;
import com.zhizus.forest.dolphin.client.ribbon.AbstractTClientFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * Created by dempezheng on 2017/8/4.
 */
public class ThriftClientFactory<T extends TServiceClient> extends AbstractTClientFactory<T> {

    public ThriftClientFactory(SpringClientFactory clientFactory) {
        super(clientFactory);
    }

    @Override
    public TTransport makeTransport(Server server) throws TTransportException {
        TSocket transport = new TSocket(server.getHost(), server.getPort());
        transport.setTimeout(5000);
        TFramedTransport tFramedTransport = new TFramedTransport(transport);
        tFramedTransport.open();
        return tFramedTransport;
    }

}

