package com.maoshen.component.dao.mapper;

/**
 * @JdxController DaoAction,业务dao类信息， 记录此DaoAction对象
 * @author dell
 *
 */
public class DaoAction {
	// 头顶注释的value值
	private String value;
	// 代理对象
	private Object proxyActionObj;
	// 最初的接口cls
	private Class<?> cls;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getProxyActionObj() {
		return proxyActionObj;
	}

	public void setProxyActionObj(Object proxyActionObj) {
		this.proxyActionObj = proxyActionObj;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

}
