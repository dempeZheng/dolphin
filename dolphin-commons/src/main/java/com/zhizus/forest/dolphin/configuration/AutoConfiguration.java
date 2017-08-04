package com.zhizus.forest.dolphin.configuration;

import com.zhizus.forest.dolphin.support.ThriftProviderAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@Configuration
@ConditionalOnWebApplication
public class AutoConfiguration implements ServletContextInitializer {

    @Bean
    public ThriftProviderAspect aspectThriftProvider() {
        return new ThriftProviderAspect();
    }

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {

    }

    @Configuration
    public static class Registrar extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {


        }
    }

}
