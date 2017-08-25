package com.zhizus.forest.dolphin.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.RibbonStatsRecorder;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/23.
 */
public class THttpLoadBalancerClient {
    private SpringClientFactory clientFactory;
    private String serviceId;
    private String listOfBackupServers;
    THttpDelegate tHttpDelegate;

    public THttpLoadBalancerClient(SpringClientFactory clientFactory, String serviceId, String listOfBackupServers,
                                   THttpDelegate tHttpDelegate) {
        this.clientFactory = clientFactory;
        this.serviceId = serviceId;
        this.listOfBackupServers = listOfBackupServers;
        this.tHttpDelegate = tHttpDelegate;
    }


    public ClientHttpResponse execute(String path, byte[] body) throws IOException {
        if (Strings.isNullOrEmpty(serviceId)) {
            serviceId = "default";
        }
        Server server = choose(serviceId);
        RibbonServer ribbonServer = new RibbonServer(serviceId, server, path);
        RibbonLoadBalancerContext context = this.clientFactory.getLoadBalancerContext(serviceId);
        RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);
        try {
            ClientHttpResponse response = tHttpDelegate.execute(ribbonServer.getUri(), body);
            statsRecorder.recordStats(response);
            return response;
        } catch (IOException ex) {
            statsRecorder.recordStats(ex);
            throw ex;
        } catch (Exception ex) {
            statsRecorder.recordStats(ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }


    public Server choose(String serviceId) {
        ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
        Server server = getServer(loadBalancer);
        if (server == null && !Strings.isNullOrEmpty(listOfBackupServers)) {
            loadBalancer.addServers(backupServerList(listOfBackupServers));
            server = getServer(loadBalancer);
        }
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        return server;
    }


    private List<Server> backupServerList(String backupServers) {
        return derive(backupServers);
    }

    private List<Server> derive(String value) {
        List<Server> list = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(value)) {
            for (String s : value.split(",")) {
                list.add(new Server(s.trim()));
            }
        }
        return list;
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
        private final String path;
        private Map<String, String> metadata;

        protected RibbonServer(String serviceId, Server server, String path) {
            this(serviceId, server, false, Collections.<String, String>emptyMap(), path);
        }

        protected RibbonServer(String serviceId, Server server, boolean secure,
                               Map<String, String> metadata, String path) {
            this.serviceId = serviceId;
            this.server = server;
            this.secure = secure;
            this.metadata = metadata;
            this.path = path;
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
            Assert.notNull(server, "instance can not be null");
            String url = "http://" + server.getHost() + ":" + server.getPort() + path;
            URI uri = URI.create(url);
            if (secure) {
                uri = UriComponentsBuilder.fromUri(uri).scheme("https").build().toUri();
            }
            return uri;
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
