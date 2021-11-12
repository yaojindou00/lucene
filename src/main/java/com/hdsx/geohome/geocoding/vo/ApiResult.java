package com.hdsx.geohome.geocoding.vo;

import java.io.Serializable;

/**
 * Created by fk on 2018/5/20.
 */
public class ApiResult implements Serializable {

    private static final long serialVersionUID = 9011442265854933082L;
    
    public static final int ERR_CODE = 500;

    public static final int SUC_CODE = 200;
    private int errCode;

    private String errMsg;

    private Object object;

    private boolean successed;

    public ApiResult() {
    }

    public ApiResult(int errCode, String errMsg, boolean successed) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.successed = successed;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }
}
