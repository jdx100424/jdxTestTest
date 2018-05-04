package com.maoshen.jdxtest.controller;

import java.util.HashMap;
import java.util.Map;

import com.maoshen.component.controller.JdxController;
import com.maoshen.component.controller.JdxControllerUrlMapper;

@JdxController(value = "jdxGoController/1/")
public class JdxGoController {
	
	@JdxControllerUrlMapper(value="testGo/")
	public Map<String,Object> testGo() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "testGo");
		return map;
	}
}