package com.maoshen.component.controller.mapper;

/**
 * @JdxControllerUrlMapper
 * 方法名和运行的controller对象,CONTROLLER真正保存的映射URL和实体类
 * @author dell
 *
 */
public class ControllerActionMethod {
	//controller类对象
	private Object actionObj;
	//方法名
	private String methodName;
	//方法参数
	private ControllerActionMethodParam[] controllerActionMethodParam;

	public ControllerActionMethodParam[] getControllerActionMethodParam() {
		return controllerActionMethodParam;
	}

	public void setControllerActionMethodParam(ControllerActionMethodParam[] controllerActionMethodParam) {
		this.controllerActionMethodParam = controllerActionMethodParam;
	}

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
