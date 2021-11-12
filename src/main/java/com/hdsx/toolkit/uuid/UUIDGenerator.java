package com.hdsx.toolkit.uuid;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by jzh on 2017/7/4.
 */
public class UUIDGenerator implements Serializable {
    private static final long serialVersionUID = 4374164547938464298L;

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}