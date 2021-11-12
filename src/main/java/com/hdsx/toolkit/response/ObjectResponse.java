package com.hdsx.toolkit.response;


import java.io.Serializable;


/**
 * 

* @Description: 服务器消息对象

* @author: <a href="mailto:jingzhh@hdsxtech.com">jingzh</a>

* @date: 2017年06月19日 上午11:30:43
*
 */
public class ObjectResponse implements Serializable{

	private static final long serialVersionUID = 6459579728006690174L;

	private boolean success = true;

	private String msg;

	private Object result;

	private String code;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ObjectResponse(boolean success, String msg, Object result, String code) {
		this.success = success;
		this.msg = msg;
		this.result = result;
		this.code = code;
	}

	public ObjectResponse() {
	}
}
