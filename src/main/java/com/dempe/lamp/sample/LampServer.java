package com.dempe.lamp.sample;

import com.dempe.lamp.BootServer;
import com.dempe.lamp.Server;
import com.dempe.lamp.utils.DefConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan
public class LampServer {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(LampServer.class);
        Server server = new BootServer(DefConfigFactory.createDEVConfig(), context);
        server.start();
    }
}
