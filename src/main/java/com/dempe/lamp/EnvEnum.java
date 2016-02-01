package com.dempe.lamp;

/**
 * 开发环境枚举类[开发，测试，线上]
 * User: Dempe
 * Date: 2016/1/14
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public enum EnvEnum {

    DEV("dev"), UAT("uat"), PROD("prod"),;

    private String env;

    private EnvEnum(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
