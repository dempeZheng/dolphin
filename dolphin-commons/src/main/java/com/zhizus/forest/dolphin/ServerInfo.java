package com.zhizus.forest.dolphin;

/**
 * Created by Dempe on 2017/8/2 0002.
 */
public class ServerInfo {
    private String id;
    private String serviceName;
    private int group;
    private short protoType;
    private String ip;
    private int port;

    public ServerInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ServerInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServerInfo setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public int getGroup() {
        return group;
    }

    public ServerInfo setGroup(int group) {
        this.group = group;
        return this;
    }

    public short getProtoType() {
        return protoType;
    }

    public ServerInfo setProtoType(short protoType) {
        this.protoType = protoType;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ServerInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerInfo setPort(int port) {
        this.port = port;
        return this;
    }

    public interface Iface {

        String getName();

    }

    public static class Test implements Iface{
        private String name;


        public Test(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
