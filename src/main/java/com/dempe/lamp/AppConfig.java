package com.dempe.lamp;

import org.aeonbits.owner.Config;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/12/11
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
@Config.Sources("classpath:application.properties")
public interface AppConfig extends Config {

    // *********************system configuration*********************
    @Key("host")
    @DefaultValue("localhost")
    String host();

    @Key("port")
    @DefaultValue("8888")
    int port();

    @DefaultValue("true")
    boolean isMaster();

    @DefaultValue("true")
    boolean tcpNoDelay();

    @DefaultValue("true")
    boolean soKeepAlive();

    @Key("common.package")
    @DefaultValue("com.dempe.lamp")
    String getPackageName();


    // ***********************application configuration*****************


}
