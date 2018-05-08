package com.maoshen.component.controller.mapper;

import com.maoshen.component.controller.annotation.JdxControllerJson;
import com.maoshen.component.controller.annotation.JdxControllerMethodParam;

/**
 * 每个controller方法中，所有参数的类型，是否为JSON标记，是否为强制标记request名称
 * @author dell
 *
 */
public class ControllerActionMethodParam {
	//是否为强制标记request名称
	private JdxControllerMethodParam jdxControllerMethodParam;
	//是否为JSON标记
	private JdxControllerJson jdxControllerJson;
	//所有参数的类型
	private Class<?> param;

	public JdxControllerJson getJdxControllerJson() {
		return jdxControllerJson;
	}

	public void setJdxControllerJson(JdxControllerJson jdxControllerJson) {
		this.jdxControllerJson = jdxControllerJson;
	}

	public JdxControllerMethodParam getJdxControllerMethodParam() {
		return jdxControllerMethodParam;
	}

	public void setJdxControllerMethodParam(JdxControllerMethodParam jdxControllerMethodParam) {
		this.jdxControllerMethodParam = jdxControllerMethodParam;
	}

	public Class<?> getParam() {
		return param;
	}

	public void setParam(Class<?> param) {
		this.param = param;
	}
}
