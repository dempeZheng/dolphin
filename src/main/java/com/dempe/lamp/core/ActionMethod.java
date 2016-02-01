package com.dempe.lamp.core;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * action方法类
 * User: Dempe
 * Date: 2015/10/15
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class ActionMethod {

    private Object target;

    private Method method;


    /**
     * @param target
     * @param method
     */
    public ActionMethod(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object call(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ActionMethod{" +
                "target=" + target +
                ", method=" + method +
                '}';
    }
}
