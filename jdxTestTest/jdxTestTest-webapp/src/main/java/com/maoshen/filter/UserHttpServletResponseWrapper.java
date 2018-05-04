package com.maoshen.filter;

import java.io.IOException;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * http://www.myhack58.com/Article/html/3/7/2012/36142_6.htm
 * 
 * 
 * @since 2014年8月14日 上午10:03:26
 */
public class UserHttpServletResponseWrapper extends HttpServletResponseWrapper {
	public UserHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	private int httpStatus;

	@Override
	public void sendError(int sc) throws IOException {
		httpStatus = sc;
		//super.sendError(sc);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpStatus = sc;
		//super.sendError(sc, msg);
	}

	@Override
	public void setStatus(int sc) {
		httpStatus = sc;
		super.setStatus(sc);
	}

	public int getStatus() {
		return httpStatus;
	}
}