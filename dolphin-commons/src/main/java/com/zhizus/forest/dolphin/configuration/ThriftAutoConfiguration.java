package com.zhizus.forest.dolphin.configuration;

import com.zhizus.forest.dolphin.annotation.ThriftService;
import com.zhizus.forest.dolphin.server.AbstractThriftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TProcessor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

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
            if (bean == null) {
                continue;
            }

            Class<? extends TBaseProcessor> aClass = thriftService.processorType();
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Class<?>[] interfaces = targetClass.getInterfaces();
            Class<?> paramType = getConstructorPramType(interfaces);
            if (paramType == null) {
                //
                continue;
            }
            Class[] parameterTypes = {paramType};
            Constructor<? extends TBaseProcessor> constructor1 = aClass.getConstructor(parameterTypes);
            TBaseProcessor processor = constructor1.newInstance(bean);

            AbstractThriftServer server = new AbstractThriftServer() {
                @Override
                public int getPort() {
                    return thriftService.port();
                }

                @Override
                public TProcessor getProcessor() {

                    return processor;
                }
            };


            server.setThriftServerName(thriftService.value()[0]);
            server.start();


        }
    }

    private Class<?> getConstructorPramType(Class<?>[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            return null;
        }
        if (interfaces.length == 1) {
            return interfaces[0];
        }
        for (Class<?> anInterface : interfaces) {
            if (StringUtils.endsWith(anInterface.getName(), "$Iface")) {
                return anInterface;
            }
        }
        return null;
    }
}
