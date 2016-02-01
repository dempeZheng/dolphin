package com.dempe.lamp.core;


import java.lang.reflect.InvocationTargetException;

/**
 * 方法执行类
 * User: Dempe
 * Date: 2015/11/4
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class MethodInvoker {


    public static Object interceptorInvoker(ActionMethod actionMethod, Object[] parameterValues)
            throws InvocationTargetException, IllegalAccessException {

        // 速率限定

        // 拦截器前

        Object result = actionMethod.call(parameterValues);

        return result;
    }
}
