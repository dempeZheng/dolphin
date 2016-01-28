package com.dempe.lamp.proto;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.codec.pack.Marshallable;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public interface Request extends Marshallable {
    /**
     * 获取路由uri
     *
     * @return
     */
    String uri();


    /**
     * 获取请求参数
     *
     * @return
     */
    JSONObject getParamJSON();
}
