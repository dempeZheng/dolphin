package com.dempe.lamp.utils;


import com.dempe.lamp.AppConfig;
import com.dempe.lamp.EnvEnum;
import org.aeonbits.owner.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/14
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class DefConfigFactory {
    public static AppConfig createUATConfig() {
        return createConfig(EnvEnum.UAT.getEnv());
    }

    public static AppConfig createDEVConfig() {
        return createConfig(EnvEnum.DEV.getEnv());
    }

    public static AppConfig createPRODConfig() {
        return createConfig(EnvEnum.PROD.getEnv());
    }

    public static AppConfig createConfig(String env) {
        Map myVars = new HashMap();
        myVars.put("env", env);
        System.setProperty("env", env);
        return ConfigFactory.create(AppConfig.class, myVars);
    }


}
