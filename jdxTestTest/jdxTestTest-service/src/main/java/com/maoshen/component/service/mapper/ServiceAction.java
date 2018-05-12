package com.maoshen.component.service.mapper;

import java.lang.reflect.Field;

/**
 * @JdxController
 * ServiceAction,业务service类信息，
 * 记录此ServiceAction对象,里面的参数信息
 * @author dell
 *
 */
public class ServiceAction {
	//头顶注释的value值
	private String value;
	//对象
	private Object actionObj;
	//对象属性
	private Object[] paramObjArr = null;
	
	private Field[] fieldArr= null;
	
	public Field[] getFieldArr() {
		return fieldArr;
	}

	public void setFieldArr(Field[] fieldArr) {
		this.fieldArr = fieldArr;
	}

	public Object getActionObj() {
		return actionObj;
	}

	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}

	public Object[] getParamObjArr() {
		return paramObjArr;
	}

	public void setParamObjArr(Object[] paramObjArr) {
		this.paramObjArr = paramObjArr;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
