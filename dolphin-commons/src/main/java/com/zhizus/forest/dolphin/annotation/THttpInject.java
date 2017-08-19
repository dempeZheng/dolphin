package com.zhizus.forest.dolphin.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Created by dempezheng on 2017/8/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
//@Autowired
public @interface THttpInject {

    String value() default "";

    String serviceName() default "";

    String[] serverArr() default {};

    String path() default "/";
}
