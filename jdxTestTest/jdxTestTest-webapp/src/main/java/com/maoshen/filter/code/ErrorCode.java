package com.maoshen.filter.code;

public enum ErrorCode {
	HTTP_OK(200,"jdx_200"),
	HTTP_NOT_FOUNT(404,"jdx_404"),
	SYSTEM_ERROR(500,"jdx_500");
	
	private int code;
	private String msg;
	private ErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
