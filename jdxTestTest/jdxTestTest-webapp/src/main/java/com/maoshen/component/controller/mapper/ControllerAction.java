package com.maoshen.component.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @JdxController
 * ControllerAction,最外面的Controller类信息，
 * 记录此ControllerAction对象及头顶@JdxController设置的URL
 * @author dell
 *
 */
public class ControllerAction {
	private Object actionObj;
	
	private String urlMapperStr = null;

	public String getUrlMapperStr() {
		return urlMapperStr;
	}

	public void setUrlMapperStr(String urlMapperStr) {
		this.urlMapperStr = urlMapperStr;
	}

	private Map<String, String> methodUrlMapper = new HashMap<String, String>();

	public Object getActionObj() {
		return actionObj;
	}

	public void setActionObj(Object actionObj) {
		this.actionObj = actionObj;
	}

	public Map<String, String> getMethodUrlMapper() {
		return methodUrlMapper;
	}

	public void setMethodUrlMapper(Map<String, String> methodUrlMapper) {
		this.methodUrlMapper = methodUrlMapper;
	}

	public String getMethodUrlMappingValue(String target) {
		if(StringUtils.isNotBlank(target)) {
			String newStr = "";
			String []arr = target.split("/");
			if(arr.length>1) {
				for(int i=1;i<arr.length;i++) {
					newStr = newStr + "/" + arr[i];
				}
			}
			return newStr;
		}
		return null;
	}
}
