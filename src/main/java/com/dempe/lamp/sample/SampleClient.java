package com.dempe.lamp.sample;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.client.JSONClient;
import com.dempe.lamp.client.ReplyFuture;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.proto.json.JSONRequest;
import com.dempe.lamp.utils.MetricThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 简单client示例
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class SampleClient {

    public static void main(String[] args) throws InterruptedException {


        MetricThread thread = new MetricThread("client");
        List<JSONClient> clientList = new ArrayList<JSONClient>();

        int size = 8;
        for (int i = 0; i < size; i++) {
            clientList.add(new JSONClient("localhost", 8888));
        }
        int i = 0;
        while (true) {
            i++;
            thread.increment();
            JSONClient client = clientList.get(i % size);
            // 初始化client
            JSONObject data = new JSONObject();
            data.put("name", "dempe");
            // 构造json请求协议
            JSONRequest request = new JSONRequest("/sample/hello", data);
            //发送请求并返回响应

            ReplyFuture replyFuture = client.sendAndWaitNew(request);

            if (i % 80000 == 0) {
                TimeUnit.SECONDS.sleep(1);
//                System.out.println(replyFuture.getReply());
//                System.out.println("-----------" +replyFuture.getReply());
            }
        }

    }
}
