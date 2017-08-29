package com.zhizus.forest.dolphin.client;

import com.zhizus.forest.dolphin.ribbon.THttpClient;
import com.zhizus.forest.dolphin.ribbon.THttpTemplate;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by dempezheng on 2017/8/22.
 */
public class TServiceClientFactory {

    private  THttpTemplate tHttpTemplate;

    public TServiceClientFactory( THttpTemplate tHttpTemplate) {
        this.tHttpTemplate = tHttpTemplate;
    }

    public <T extends TServiceClient> T applyClient(TServiceBuilder builder, Class<T> clazz) throws TTransportException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        TBinaryProtocol tBinaryProtocol = makeProtocol(builder);
        Class[] parameterTypes = {org.apache.thrift.protocol.TProtocol.class};
        Constructor constructor = clazz.getConstructor(parameterTypes);
        T client = (T) constructor.newInstance(tBinaryProtocol);
        return client;
    }

    private TBinaryProtocol makeProtocol(TServiceBuilder builder) throws TTransportException {
        String path = builder.getPath();
        String backupServers = builder.getBackupOfServerList();
        String serviceId = builder.getServiceId();
        THttpClient trans = new THttpClient(tHttpTemplate, serviceId, path);
        return new TBinaryProtocol(trans);
    }


    public static class TServiceBuilder {
        private String path;
        private String serviceId;
        private String backupOfServerList;

        public String getPath() {
            return path;
        }

        public TServiceBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public String getServiceId() {
            return serviceId;
        }

        public TServiceBuilder withServiceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public String getBackupOfServerList() {
            return backupOfServerList;
        }

        public TServiceBuilder withBackupOfServerList(String backupOfServerList) {
            this.backupOfServerList = backupOfServerList;
            return this;
        }
    }


}
