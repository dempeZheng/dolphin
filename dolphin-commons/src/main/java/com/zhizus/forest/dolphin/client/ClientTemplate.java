package com.zhizus.forest.dolphin.client;

/**
 * Created by dempezheng on 2017/8/4.
 */
public interface ClientTemplate<T> {

    T newClient() throws Exception;

    T newClient(Class<T> aClass) throws Exception;

}
