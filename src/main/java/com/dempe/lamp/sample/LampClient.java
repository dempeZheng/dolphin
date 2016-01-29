package com.dempe.lamp.sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.client.ReplyClient;
import com.dempe.lamp.proto.json.JSONRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class LampClient {
    public static void main(String[] args) {
//        ReplyClient client = new ReplyClient("localhost", 8888);
        JSONObject data = new JSONObject();
        data.put("name", "dempe");
        JSONRequest request = new JSONRequest("/lamp/test", data);
        System.out.println(request);
        System.out.println(JSON.toJSONString(request));
//        Response response = client.sendAndWait(request);
//        client.sendOnly(request);
//        System.out.println(response);
    }
}
