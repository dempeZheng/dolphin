package com.zhizus.forest.dolphin.annotation;

import com.zhizus.forest.dolphin.configuration.THttpInjectRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * Created by dempezheng on 2017/8/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(THttpInjectRegistrar.class)
public @interface EnableTHttpInject {

    int order() default Ordered.HIGHEST_PRECEDENCE;
}
