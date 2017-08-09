package com.zhizus.forest.dolphin;

import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpTemplate;
import com.zhizus.forest.dolphin.client.ribbon.thrift.ThriftTemplate;
import com.zhizus.forest.dolphin.gen.Sample;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dempezheng on 2017/8/3.
 */
@Configuration
//@ConditionalOnClass(ThriftTemplate.class)
public class DolphinLoadBalanceAutoConfiguration {

    @Bean
    public THttpTemplate<Sample.Client> initSampleClient(SpringClientFactory springClientFactory) {
        THttpTemplate<Sample.Client> template = new THttpTemplate<Sample.Client>("dolphin-server", Sample.Client.class,
                springClientFactory);
        return template;
    }

    @Bean
    @LoadBalanced
    public ThriftTemplate<Sample.Client> initThriftClient(SpringClientFactory springClientFactory) {
        ThriftTemplate<Sample.Client> template = new ThriftTemplate<Sample.Client>("dolphin-server", Sample.Client.class,
                springClientFactory);
        return template;

    }

}
