package com.dempe.lamp.core;

import java.lang.reflect.InvocationTargetException;

/**
 * 业务执行快照
 * 通过一个request获取一个response
 * User: zhengdaxia
 * Date: 15/10/17
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public interface Take<R, T> {

    T act(R request) throws InvocationTargetException, IllegalAccessException;


}
