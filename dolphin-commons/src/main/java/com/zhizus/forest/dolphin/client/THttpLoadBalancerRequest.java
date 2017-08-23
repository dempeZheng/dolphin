package com.zhizus.forest.dolphin.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;


/**
 * Created by dempezheng on 2017/8/23.
 */
public class THttpLoadBalancerRequest {

    private HttpClient client;

    public THttpLoadBalancerRequest(HttpClient client) {
        this.client = client;
    }

    public HttpClient getClient() {
        return client;
    }

    public HttpResponse apply(ServiceInstance instance, byte[] body) throws Exception {
        URI uri = instance.getUri();
        HttpPost post = new HttpPost();
        post.setHeader("Content-Type", "application/x-thrift");
        post.setHeader("Accept", "application/x-thrift");
        post.setHeader("User-Agent", "Java/THttpClient/HC");
        post.setEntity(new ByteArrayEntity(body));
        post.setURI(uri);
        return client.execute(post);
    }
}
