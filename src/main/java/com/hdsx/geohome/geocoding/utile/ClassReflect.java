package com.hdsx.geohome.geocoding.utile;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by jzh on 2017/7/23.
 */
public class ClassReflect extends ReflectionUtils {

    public static Field[] getDeclaredFields(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        return fields;
    }

}
