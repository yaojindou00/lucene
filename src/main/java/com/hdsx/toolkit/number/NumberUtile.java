package com.hdsx.toolkit.number;

import java.math.BigDecimal;


/**
 * Created by jzh on 2017/7/4.
 */
public class NumberUtile  {

    public static long float2long(float value){
        BigDecimal decimal = new BigDecimal(value);
        return decimal.longValue();
    }

    public static float string2float(String value){
        if(value == null){
            return 0.0f;
        }
        BigDecimal decimal = new BigDecimal(value);
        return decimal.floatValue();
    }


}
