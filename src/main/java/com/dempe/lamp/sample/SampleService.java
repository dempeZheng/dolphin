package com.dempe.lamp.sample;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * sample的service示例
 * User: Dempe
 * Date: 2016/1/28
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SampleService {

    public JSONObject hello(String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 6666);
        jsonObject.put("name", name);
        return jsonObject;
    }
}
