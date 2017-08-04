package com.zhizus.forest.dolphin.configuration;

import com.zhizus.forest.dolphin.annotation.ThriftService;
import com.zhizus.forest.dolphin.server.AbstractThriftServer;
import com.zhizus.forest.dolphin.server.ProcessorFactory;
import org.apache.thrift.TProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dempezheng on 2017/8/4.
 */
@Configuration
@ConditionalOnClass(ThriftService.class)
@ConditionalOnWebApplication
public class ThriftAutoConfiguration implements ApplicationContextAware, InitializingBean {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] beanNamesForAnnotation = context.getBeanNamesForAnnotation(ThriftService.class);
        for (String beanName : beanNamesForAnnotation) {
            ThriftService thriftService = context.findAnnotationOnBean(beanName, ThriftService.class);
            Object bean = context.getBean(beanName);
            if (!(bean instanceof ProcessorFactory)) {
                return;

            }
            new AbstractThriftServer() {
                @Override
                public int getPort() {
                    return thriftService.port();
                }

                @Override
                public TProcessor getProcessor() {
                    return ((ProcessorFactory) bean).getProcessor();
                }
            }.start();


        }
    }
}
