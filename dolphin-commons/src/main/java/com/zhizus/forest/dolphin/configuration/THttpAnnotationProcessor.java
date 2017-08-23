package com.zhizus.forest.dolphin.configuration;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.client.TServiceProxyClientFactory;
import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * Created by dempezheng on 2017/8/16.
 */
public class THttpAnnotationProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware, PriorityOrdered {
    private final static Logger logger = LoggerFactory.getLogger(THttpAnnotationProcessor.class);

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {
        Class<?> targetClass = bean.getClass();
        do {
            ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    THttpInject annotation = AnnotationUtils.getAnnotation(field, THttpInject.class);
                    if (annotation != null) {
                        System.out.println(field);
                    }
                    try {
                        processFields(bean, field);
                    } catch (Exception e) {
                        logger.info(e.getMessage(), e);
                        throw new BeanCreationException(beanName, "inject tHttpClient err");
                    }

                }
            });
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return pvs;
    }

    private void processFields(Object bean, Field field) throws Exception {
        THttpInject annotation = AnnotationUtils.getAnnotation(field, THttpInject.class);
        if (annotation == null) {
            return;
        }
        Preconditions.checkArgument(TServiceClient.class.isAssignableFrom(field.getType()),
                "Invalid type: %s for field: %s, should be Config", field.getType(), field);

        String beanName = annotation.value();
        if (Strings.isNullOrEmpty(beanName)) {
            beanName = field.getName();
        }
        Object tHttpClient = null;
        if (beanFactory.containsBean(beanName)) {
            tHttpClient = beanFactory.getBean(beanName);
        } else {
            SpringClientFactory springClientFactory = beanFactory.getBean(SpringClientFactory.class);
            TServiceProxyClientFactory factory = new TServiceProxyClientFactory(springClientFactory);
            tHttpClient = factory.applyProxyClient(field, annotation);
            beanFactory.registerSingleton(beanName, tHttpClient);
        }
        if (tHttpClient != null) {
            ReflectionUtils.makeAccessible(field);
            field.set(bean, tHttpClient);
        }
    }


    @Override
    public int getOrder() {
        //make it as late as possible
        return Ordered.LOWEST_PRECEDENCE - 2;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


}

