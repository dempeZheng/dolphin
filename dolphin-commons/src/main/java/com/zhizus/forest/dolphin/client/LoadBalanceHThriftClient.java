package com.zhizus.forest.dolphin.client;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.netflix.ribbon.*;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class LoadBalanceHThriftClient implements LoadBalancerClient {

    private SpringClientFactory clientFactory;

    public LoadBalanceHThriftClient(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }


    @Override
    public ServiceInstance choose(String serviceId) {
        return null;
    }



    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
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
            T returnVal = request.apply(ribbonServer);
            statsRecorder.recordStats(returnVal);
            return returnVal;
        }
        // catch IOException and rethrow so RestTemplate behaves correctly
        catch (IOException ex) {
            statsRecorder.recordStats(ex);
            throw ex;
        }
        catch (Exception ex) {
            statsRecorder.recordStats(ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    @Override
    public URI reconstructURI(ServiceInstance instance, URI original) {
        return null;
    }

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }


    protected Server getServer(ILoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            return null;
        }
        return loadBalancer.chooseServer("default"); // TODO: better handling of key
    }


    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        if (config != null) {
            Boolean isSecure = config.get(CommonClientConfigKey.IsSecure);
            if (isSecure != null) {
                return isSecure;
            }
        }

        return serverIntrospector(serviceId).isSecure(server);
    }


    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }



    protected static class RibbonServer implements ServiceInstance {
        private final String serviceId;
        private final Server server;
        private final boolean secure;
        private Map<String, String> metadata;

        protected RibbonServer(String serviceId, Server server) {
            this(serviceId, server, false, Collections.<String, String> emptyMap());
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
