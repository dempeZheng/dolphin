package com.dempe.lamp.proto;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.utils.pack.Marshallable;

/**
 * 请求消息
 * User: Dempe
 * Date: 2016/1/28
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public interface Request extends Marshallable {

    /**
     * 获取消息id标识
     *
     * @return
     */
    int getId();

    /**
     * set消息id标志
     *
     * @param id
     */
    void setId(int id);

    /**
     * 获取路由uri
     *
     * @return
     */
    String getUri();


    /**
     * 获取请求参数
     *
     * @return
     */
    JSONObject getData();
}
