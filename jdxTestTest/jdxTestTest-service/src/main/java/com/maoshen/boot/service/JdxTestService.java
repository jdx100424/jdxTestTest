package com.maoshen.boot.service;

import java.util.UUID;

import com.maoshen.boot.dao.JdxTestDao;
import com.maoshen.component.dao.annotation.JdxDaoInject;
import com.maoshen.component.service.annotation.JdxService;
import com.maoshen.component.service.annotation.JdxServiceInject;

@JdxService("jdxTestService")
public class JdxTestService {
	@JdxDaoInject("jdxTestDao")
	public JdxTestDao jdxTestDao;
	
	@JdxServiceInject("jdxTestSubService")
	public JdxTestSubService jdxTestSubService;
	
	public String sayHello(String s) {
		System.out.println(jdxTestSubService.saySubHello(s));
		Integer daoResult = jdxTestDao.getCount(1L);
		jdxTestDao.insert(System.currentTimeMillis(), UUID.randomUUID().toString());
		System.out.println("jdxTestDao:" + daoResult);
		return System.currentTimeMillis() + ":"+ daoResult+ ":hello:"+s;
	}
}
