package com.hdsx.geohome.geocoding;

import com.alibaba.fastjson.serializer.SerializerFeature;

import com.hdsx.box.net.filter.CrossDomainFilter;
import com.hdsx.toolkit.json.FastJsonHttpMessageConverter;
import com.hdsx.toolkit.json.GeometryHttpMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;


/**
 * Created by jingzh on 2017/6/29.
 */
@Configuration
@EnableCaching
@SpringBootApplication
public class GeocodingApplication {

    @Bean
    public ThreadPoolTaskExecutor registerTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setKeepAliveSeconds(3000000);
        taskExecutor.setQueueCapacity(500);
        return taskExecutor;
    }

    @Bean
    public FilterRegistrationBean addCrossDomainFilter(){
        FilterRegistrationBean registrationBean=new  FilterRegistrationBean(new CrossDomainFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean addEncodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        FilterRegistrationBean registrationBean=new  FilterRegistrationBean(encodingFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        fastConverter.setFeatures(SerializerFeature.PrettyFormat);
        GeometryHttpMessageConverter geometryHttpMessageConverter = new GeometryHttpMessageConverter();
        return new HttpMessageConverters(fastConverter,geometryHttpMessageConverter);
    }

    public static void main(String[] args) {
        SpringApplication.run(GeocodingApplication.class, args);
    }

}
