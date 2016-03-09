package com.dempe.lamp.sample;

import com.dempe.lamp.client.Future;
import com.dempe.lamp.client.FutureClient;
import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.utils.MetricThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 简单client示例
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class SampleClient {

    public static void main(String[] args) throws Exception {

        stressTesting();

    }


    public static void simpleTesting() throws Exception {
        FutureClient client = new FutureClient("localhost", 8888);
        // 构造json请求协议
        Request request = buildRequest();
        Future<Response> future = client.send(request);
        System.out.println(future.await());
    }

    public static void stressTesting() throws Exception {
        MetricThread thread = new MetricThread("client");
        List<FutureClient> clientList = new ArrayList<FutureClient>();

        int size = 8;
        for (int i = 0; i < size; i++) {
            clientList.add(new FutureClient("localhost", 8888));
        }
        int i = 0;
        while (true) {
            i++;
            thread.increment();
            FutureClient client = clientList.get(i % size);
            // 初始化client
            Request request = buildRequest();
            //发送请求并返回响应
            Future<Response> future = client.send(request);


            if (i % 100000 == 0) {
                TimeUnit.SECONDS.sleep(1);
//                System.out.println(future.await());
//                System.out.println("-----------" +replyFuture.getReply());
            }
        }

    }

    public static Request buildRequest() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "dempe");
        data.put("age","1");
        Request request = new Request();
        request.setUri("/sample/hello");
        request.setParamMap(data);
        return request;
    }
}
