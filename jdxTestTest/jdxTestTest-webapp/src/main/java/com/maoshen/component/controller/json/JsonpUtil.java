package com.maoshen.component.controller.json;

import com.alibaba.fastjson.JSONObject;

public class JsonpUtil {
	public static String restJsonp(String callback, Object obj) {
		String result = JSONObject.toJSONString(obj);
		if (callback != null) {
			return new StringBuilder().append(callback).append("(").append(result).append(")").toString();
		} else {
			return result;
		}
	} 
}
