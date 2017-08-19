package com.zhizus.forest.dolphin;


import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.demo.ServerApplication;
import com.zhizus.forest.dolphin.gen.Sample;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Dempe on 2017/7/1 0001.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServerApplication.class)
@WebAppConfiguration
public class HttpSampleTest {

    @THttpInject(value = "tHttpClient", serverArr = {"localhost:9090"}, path = "/sample",serviceName = "dolphin-server")
    Sample.Client tHttpClient;

//    @Autowired
//    Sample.Client client;

    @Test
    public void test2() throws TException {
        String hello = tHttpClient.hello("hello");
        System.out.println(hello);
        String hello2 = tHttpClient.hello("hello");
        String hello3 = tHttpClient.hello("hello");


    }

    @Test
    public void beanProxyFactoryTest() throws TException {
         THttpClient transport;
         Sample.Client sampleClient;
        transport = new THttpClient("http://localhost:9090/sample");
//        transport.setCustomHeader("protocol", "json");
        TProtocol protocol = new TBinaryProtocol(transport);
        sampleClient = new Sample.Client(protocol);

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(sampleClient);
        Sample.Client object = (Sample.Client) proxyFactoryBean.getObject();
        System.out.println(object.hello("6666"));
    }


    @Test
    public void beanProxyFactoryTest2() throws TException {
        ServerInfo.Test serverInfo =  new ServerInfo.Test("666");

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(serverInfo);
        proxyFactoryBean.setProxyTargetClass(true);
        ServerInfo.Test info= ( ServerInfo.Test) proxyFactoryBean.getObject();
        System.out.println(info.getName());
    }

}
