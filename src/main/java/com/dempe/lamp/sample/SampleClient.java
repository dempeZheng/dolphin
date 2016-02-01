package com.dempe.lamp.sample;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.client.ReplyClient;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.proto.json.JSONRequest;

/**
 * 简单client示例
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class SampleClient {

    public static void main(String[] args) {
        // 初始化client
        ReplyClient client = new ReplyClient("localhost", 8888);
        JSONObject data = new JSONObject();
        data.put("name", "dempe");
        // 构造json请求协议
        JSONRequest request = new JSONRequest("/sample/hello", data);
        //发送请求并返回响应
        Response response = client.sendAndWait(request);
        System.out.println(response);
    }
}
