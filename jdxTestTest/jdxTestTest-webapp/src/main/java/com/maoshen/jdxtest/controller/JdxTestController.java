package com.maoshen.jdxtest.controller;

import java.util.HashMap;
import java.util.Map;

import com.maoshen.component.controller.JdxController;
import com.maoshen.component.controller.JdxControllerUrlMapper;

@JdxController(value = "/jdxTestController")
public class JdxTestController {
	
	@JdxControllerUrlMapper(value="/testJson")
	public Map<String,Object> testJson() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "jdxmaoshen");
		return map;
	}
}