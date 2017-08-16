package com.zhizus.forest.dolphin.support;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zhizus.forest.dolphin.annotation.THttpInject;
import com.zhizus.forest.dolphin.exception.DolphinFrameException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by dempezheng on 2017/8/16.
 */
public class THttpAnnotationProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {
    private final static Logger logger = LoggerFactory.getLogger(THttpAnnotationProcessor.class);

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        try {
            processFields(bean, clazz.getDeclaredFields());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw new BeanCreationException(beanName, "inject tHttpClient err");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void processFields(Object bean, Field[] declaredFields) throws TTransportException, IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException, DolphinFrameException {
        for (Field field : declaredFields) {
            THttpInject annotation = AnnotationUtils.getAnnotation(field, THttpInject.class);
            if (annotation == null) {
                continue;
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
                Class[] parameterTypes = {org.apache.thrift.protocol.TProtocol.class};
                Constructor constructor = field.getType().getConstructor(parameterTypes);
                String path = annotation.path();
                String[] serverArr = annotation.serverArr();

                if (serverArr.length < 1) {
                    throw new DolphinFrameException();
                }
                //TODO这里后期加入负载均衡的实现
                String url = "http://" + serverArr[0] + path;
                tHttpClient = constructor.newInstance(new TBinaryProtocol(new THttpClient(url)));
            }
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, bean, tHttpClient);
        }
    }


    @Override
    public int getOrder() {
        //make it as late as possible
        return Ordered.LOWEST_PRECEDENCE;
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

