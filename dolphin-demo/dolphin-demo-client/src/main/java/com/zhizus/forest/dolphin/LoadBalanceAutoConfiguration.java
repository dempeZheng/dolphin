package com.zhizus.forest.dolphin;

import com.netflix.client.http.HttpRequest;
import com.netflix.ribbon.Ribbon;
import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpTemplate;
import com.zhizus.forest.dolphin.client.ribbon.thrift.ThriftTemplate;
import com.zhizus.forest.dolphin.gen.Sample;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dempezheng on 2017/8/3.
 */
@Configuration
//@ConditionalOnClass(ThriftTemplate.class)
public class LoadBalanceAutoConfiguration {

    @Autowired(required = false)
    private List<RibbonClientSpecification> configurations = new ArrayList<>();

    @Autowired(required = false)
    @LoadBalanced
    private List<ThriftTemplate> list = Collections.emptyList();

    @Bean
    public HasFeatures ribbonFeature() {
        return HasFeatures.namedFeature("Ribbon", Ribbon.class);
    }

    @Bean
    public SpringClientFactory springClientFactory() {
        SpringClientFactory factory = new SpringClientFactory();
        factory.setConfigurations(this.configurations);
        return factory;
    }

    @Bean
    public SmartInitializingSingleton loadBalancedRestTemplateInitializer() {
        return new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {
                for (ThriftTemplate restTemplate : LoadBalanceAutoConfiguration.this.list) {
                    System.out.println(restTemplate);
                }
            }
        };
    }


    @Bean
    public THttpTemplate<Sample.Client> initSampleClient() {
        THttpTemplate<Sample.Client> template = new THttpTemplate<Sample.Client>("dolphin-client", Sample.Client.class,
                springClientFactory());
        return template;
    }

    @Bean
    @LoadBalanced
    public ThriftTemplate<Sample.Client> initThriftClient() {
        ThriftTemplate<Sample.Client> template = new ThriftTemplate<Sample.Client>("dolphin-client-2", Sample.Client.class,
                springClientFactory());
        return template;

    }

}
