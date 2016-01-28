package com.dempe.lamp.core;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.Param;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/11/3
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public class MethodParam {

    private final static Map<Method, String[]> paramCacheMap = new ConcurrentHashMap<Method, String[]>();

    public static String[] getParameterNames(Method method) {
        String[] parameterNames = paramCacheMap.get(method);
        if (parameterNames == null) {
            parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (Annotation[] parameterAnnotation : parameterAnnotations) {
                for (int i = 0; i < parameterAnnotation.length; i++) {
                    if (parameterAnnotation[i].annotationType() == Param.class) {
                        String value = ((Param) parameterAnnotation[i]).value();
                        if (StringUtils.isNotBlank(value)) {
                            parameterNames[i] = value;
                        }
                    }
                }
            }
            // set cache
            paramCacheMap.put(method, parameterNames);
        }
        return parameterNames;
    }

    public static Object[] getParameterValues(String[] parameterNames, Method method, JSONObject params) {
        Object[] paramTarget = null;
        if (parameterNames != null) {
            paramTarget = new Object[parameterNames.length];
            Type[] type = method.getGenericParameterTypes();
            for (int i = 0; i < parameterNames.length; i++) {
                String parameterName = parameterNames[i];
                if (Integer.class == type[i] || StringUtils.equals(type[i].toString(), "int")) {
                    paramTarget[i] = params.getInteger(parameterName);
                } else if (String.class == type[i]) {
                    paramTarget[i] = params.getString(parameterName);
                } else if (Boolean.class == type[i] || StringUtils.equals(type[i].toString(), "boolean")) {
                    paramTarget[i] = params.getBoolean(parameterName);
                } else if (Long.class == type[i] || StringUtils.equals(type[i].toString(), "long")) {
                    paramTarget[i] = params.getLong(parameterName);
                } else if (Short.class == type[i] || StringUtils.equals(type[i].toString(), "short")) {
                    paramTarget[i] = params.getShort(parameterName);
                } else if (Double.class == type[i] || StringUtils.equals(type[i].toString(), "double")) {
                    paramTarget[i] = params.getDouble(parameterName);
                } else if (Float.class == type[i] || StringUtils.equals(type[i].toString(), "float")) {
                    paramTarget[i] = params.getFloat(parameterName);
                }

            }
        }
        return paramTarget;
    }


}
