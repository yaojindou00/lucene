package com.hdsx.geohome.geocoding.vo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jzh on 2017/7/23.
 */
  // 默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得，
@Retention(RetentionPolicy.RUNTIME)
public @interface NodeField {
     String value() default "";

     Class clas();

     String defaultValue() default "";
}


