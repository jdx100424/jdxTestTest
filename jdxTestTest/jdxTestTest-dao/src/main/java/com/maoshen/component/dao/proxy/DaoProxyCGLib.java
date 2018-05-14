package com.maoshen.component.dao.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;  
import net.sf.cglib.proxy.MethodInterceptor;  
import net.sf.cglib.proxy.MethodProxy;  

public class DaoProxyCGLib implements MethodInterceptor {
	private Enhancer enhancer = new Enhancer();
	private DaoProxy daoProxy = DaoProxy.getDefaultDaoProxy();

	public Object createProxy(Class<?> clazz) {
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		return enhancer.create();
	}

	// 实现MethodInterceptor接口方法
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("dao代理执行方法开始");
		// 通过代理类调用父类中的方法
		Object result = daoProxy.run(method, args);
		System.out.println("dao代理执行方法结束");
		return result;
	}
}
