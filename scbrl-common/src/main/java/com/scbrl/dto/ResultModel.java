package com.scbrl.dto;

import java.io.Serializable;

public class ResultModel implements Serializable{
	
	private static final long serialVersionUID = -5964859107304569703L;

	private String code ; // 状态标识
	
	private String msg  ; // 状态描述
	
	private Object data ; // 数据
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
