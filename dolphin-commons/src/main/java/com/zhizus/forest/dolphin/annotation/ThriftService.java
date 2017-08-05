package com.zhizus.forest.dolphin.annotation;

import org.apache.thrift.TBaseProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface ThriftService {

    String[] value() default {};

    int port() default 9000;

    Class<? extends TBaseProcessor> processorType();
}
