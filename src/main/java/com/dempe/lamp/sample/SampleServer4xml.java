package com.dempe.lamp.sample;

import com.dempe.lamp.BootServer;
import com.dempe.lamp.Server;
import com.dempe.lamp.utils.DefConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 基于spring xml的SampleServer
 * 如果业务依赖第三方bean，可以先在xml里面初始化，这样业务就可以直接注入并使用
 * User: Dempe
 * Date: 2016/1/29
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public class SampleServer4xml {
    public static void main(String[] args) {
        // 初始化spring 容器
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application.xml"});
        // 初始化并启动服务
        Server server = new BootServer(DefConfigFactory.createDEVConfig(), context);
        server.start();
    }
}
