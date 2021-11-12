package com.hdsx.geohome.geocoding.vo;

import java.io.Serializable;

/**
 * Created by jzh on 2018/4/11.
 */
public class Layer implements Serializable {

    @NodeField(value="code",clas = String.class)
    private String code;

    @NodeField(value="value",clas = String.class)
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
