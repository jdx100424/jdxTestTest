package com.maoshen.boot.service;

import com.maoshen.component.service.annotation.JdxService;
import com.maoshen.component.service.annotation.JdxServiceInject;

@JdxService("jdxTestService")
public class JdxTestService {
	
	@JdxServiceInject("jdxTestSubService")
	public JdxTestSubService jdxTestSubService;
	
	public String sayHello(String s) {
		System.out.println(jdxTestSubService.saySubHello(s));
		return System.currentTimeMillis() + ":hello:"+s;
	}
}
