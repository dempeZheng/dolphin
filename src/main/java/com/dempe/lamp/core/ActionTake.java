package com.dempe.lamp.core;

import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: zhengdaxia
 * Date: 15/10/17
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public interface ActionTake<R, T> {

    T act(R request) throws InvocationTargetException, IllegalAccessException;


}
