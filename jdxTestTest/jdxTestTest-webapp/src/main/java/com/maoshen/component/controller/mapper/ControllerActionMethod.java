package com.maoshen.component.controller.mapper;

/**
 * 方法名和运行的controller对象,CONTROLLER真正保存的映射URL和实体类
 * @author dell
 *
 */
public class ControllerActionMethod {
	private Object actionObj;

	private String methodName;

	public Object getActionObj() {
		return actionObj;
	}

	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
