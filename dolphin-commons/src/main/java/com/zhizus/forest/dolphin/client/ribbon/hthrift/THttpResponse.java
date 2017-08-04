package com.zhizus.forest.dolphin.client.ribbon.hthrift;

import com.netflix.client.ClientException;
import com.netflix.client.IResponse;
import org.apache.thrift.TServiceClient;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpResponse<T extends TServiceClient> implements IResponse{

    private T client;

    public  THttpResponse(T client) {
        this.client = client;
    }


    public THttpResponse setClient(T client) {
        this.client = client;
        return this;
    }

    @Override
    public Object getPayload() throws ClientException {
        return client;
    }

    @Override
    public boolean hasPayload() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public URI getRequestedURI() {
        return null;
    }

    @Override
    public Map<String, ?> getHeaders() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
