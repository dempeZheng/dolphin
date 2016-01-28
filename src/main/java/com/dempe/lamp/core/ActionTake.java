package com.dempe.lamp.core;


import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.proto.json.JSONResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/11/4
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public class ActionTake implements Take<Request, Response> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ActionTake.class);

    private ServerContext context;

    public ActionTake(ServerContext context) {
        this.context = context;
    }


    public Response act(Request request) throws InvocationTargetException, IllegalAccessException {
        String uri = request.uri();
        if (StringUtils.isBlank(uri)) {
            LOGGER.warn("[dispatcher]:jsonURI is blank");
            return null;
        }
        ActionMethod actionMethod = context.tackAction(uri);
        if (actionMethod == null) {
            LOGGER.warn("[dispatcher]:not find jsonURI {}", uri);
            return null;
        }
        Method method = actionMethod.getMethod();
        String[] parameterNames = MethodParam.getParameterNames(method);
        JSONObject params = request.getParamJSON();
        Object[] parameterValues = MethodParam.getParameterValues(parameterNames, method, params);
        Object result = MethodInvoker.interceptorInvoker(actionMethod, parameterValues);
        if (result == null) {
            // 当action method 返回是void的时候，不返回任何消息
            LOGGER.debug("actionMethod:{} return void.", actionMethod);
            return null;
        }
        JSONResponse resp = new JSONResponse();
        resp.setId(request.id());
        if (result instanceof JSONObject) {
            resp.setData((JSONObject) result);
        }
        System.out.println(resp.id());
        return resp;
    }


}
