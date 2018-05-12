package com.maoshen.boot.service;

import com.maoshen.component.service.annotation.JdxService;

@JdxService("jdxTestSubService")
public class JdxTestSubService {
	public String saySubHello(String s) {
		return System.currentTimeMillis() + ":subHello:"+s;
	}
}
