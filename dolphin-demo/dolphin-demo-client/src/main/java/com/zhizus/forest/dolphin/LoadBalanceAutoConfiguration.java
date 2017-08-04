package com.zhizus.forest.dolphin;

import com.netflix.client.IClient;
import com.netflix.client.http.HttpRequest;
import com.netflix.ribbon.Ribbon;
import com.zhizus.forest.dolphin.client.ribbon.hthrift.THttpTemplate;
import com.zhizus.forest.dolphin.gen.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dempezheng on 2017/8/3.
 */
@Configuration
@ConditionalOnClass({IClient.class, THttpTemplate.class})
@RibbonClients
@AutoConfigureAfter(name = "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration")
@AutoConfigureBefore(LoadBalancerAutoConfiguration.class)
public class LoadBalanceAutoConfiguration {

    @Autowired(required = false)
    private List<RibbonClientSpecification> configurations = new ArrayList<>();

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
    public THttpTemplate<Sample.Client> initSampleClient() {
        THttpTemplate<Sample.Client> template = new THttpTemplate<Sample.Client>("dolphin-client", Sample.Client.class,
                springClientFactory());
        return template;

    }

    @Configuration
    @ConditionalOnClass(HttpRequest.class)
    @ConditionalOnProperty(value = "ribbon.http.client.enabled", matchIfMissing = false)
    protected static class RibbonClientConfig {

        @Autowired
        private SpringClientFactory springClientFactory;

        @Bean
        public RestTemplateCustomizer restTemplateCustomizer(
                final RibbonClientHttpRequestFactory ribbonClientHttpRequestFactory) {
            return new RestTemplateCustomizer() {
                @Override
                public void customize(RestTemplate restTemplate) {
                    restTemplate.setRequestFactory(ribbonClientHttpRequestFactory);
                }
            };
        }

        @Bean
        public RibbonClientHttpRequestFactory ribbonClientHttpRequestFactory() {
            return new RibbonClientHttpRequestFactory(this.springClientFactory);
        }
    }
}
