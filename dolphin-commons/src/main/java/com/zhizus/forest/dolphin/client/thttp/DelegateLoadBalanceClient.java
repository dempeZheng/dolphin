package com.zhizus.forest.dolphin.client.thttp;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.ribbon.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/16.
 */
public class DelegateLoadBalanceClient {

    private HttpClient client;

    private SpringClientFactory clientFactory;

    private String serviceId;

    public DelegateLoadBalanceClient(SpringClientFactory clientFactory, HttpClient client, String serviceId) {
        this.clientFactory = clientFactory;
        this.client = client;
        this.serviceId = serviceId;
    }


    public URI reconstructURI(ServiceInstance instance, URI original) {
        Assert.notNull(instance, "instance can not be null");
        String serviceId = instance.getServiceId();
        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);
        Server server = new Server(instance.getHost(), instance.getPort());
        boolean secure = isSecure(server, serviceId);
        URI uri = original;
        if (secure) {
            uri = UriComponentsBuilder.fromUri(uri).scheme("https").build().toUri();
        }
        return context.reconstructURIWithServer(server, uri);
    }

    public HttpResponse execute(HttpPost post) throws IOException {
        return execute(serviceId, post);
    }

    public HttpResponse execute(String serviceId, HttpPost post) throws IOException {
        ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
        Server server = getServer(loadBalancer);
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
                serviceId), serverIntrospector(serviceId).getMetadata(server));

        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);
        RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);

        try {
            HttpResponse response = client.execute(post);
            statsRecorder.recordStats(response);
            return response;
        }
        // catch IOException and rethrow so RestTemplate behaves correctly
        catch (IOException ex) {
            statsRecorder.recordStats(ex);
            throw ex;
        } catch (Exception ex) {
            statsRecorder.recordStats(ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }

    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        if (config != null) {
            return config.get(CommonClientConfigKey.IsSecure, false);
        }

        return serverIntrospector(serviceId).isSecure(server);
    }

    protected Server getServer(String serviceId) {
        return getServer(getLoadBalancer(serviceId));
    }

    protected Server getServer(ILoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            return null;
        }
        return loadBalancer.chooseServer("default"); // TODO: better handling of key
    }

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }

    protected static class RibbonServer implements ServiceInstance {
        private final String serviceId;
        private final Server server;
        private final boolean secure;
        private Map<String, String> metadata;

        protected RibbonServer(String serviceId, Server server) {
            this(serviceId, server, false, Collections.<String, String>emptyMap());
        }

        protected RibbonServer(String serviceId, Server server, boolean secure,
                               Map<String, String> metadata) {
            this.serviceId = serviceId;
            this.server = server;
            this.secure = secure;
            this.metadata = metadata;
        }

        @Override
        public String getServiceId() {
            return this.serviceId;
        }

        @Override
        public String getHost() {
            return this.server.getHost();
        }

        @Override
        public int getPort() {
            return this.server.getPort();
        }

        @Override
        public boolean isSecure() {
            return this.secure;
        }

        @Override
        public URI getUri() {
            return DefaultServiceInstance.getUri(this);
        }

        @Override
        public Map<String, String> getMetadata() {
            return this.metadata;
        }

        public Server getServer() {
            return this.server;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("RibbonServer{");
            sb.append("serviceId='").append(serviceId).append('\'');
            sb.append(", server=").append(server);
            sb.append(", secure=").append(secure);
            sb.append(", metadata=").append(metadata);
            sb.append('}');
            return sb.toString();
        }
    }


}
