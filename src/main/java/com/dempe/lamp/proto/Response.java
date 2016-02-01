package com.dempe.lamp.proto;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.utils.pack.Marshallable;

/**
 * 返回消息
 * User: Dempe
 * Date: 2016/1/28
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
public interface Response extends Marshallable {

    /**
     * 获取消息id
     *
     * @return
     */
    int getId();


    void setId(int id);

    void setData(JSONObject data);


}
