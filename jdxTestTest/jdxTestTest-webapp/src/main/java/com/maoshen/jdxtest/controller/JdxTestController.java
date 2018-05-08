package com.maoshen.jdxtest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maoshen.component.controller.annotation.JdxController;
import com.maoshen.component.controller.annotation.JdxControllerMethodParam;
import com.maoshen.component.controller.annotation.JdxControllerUrlMapper;

@JdxController(value = "/jdxTestController")
public class JdxTestController {
	@JdxControllerUrlMapper(value="/testJsonNoParam")
	public Map<String,Object> testJson11111() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "jdxmaoshen");
		return map;
	}
	@JdxControllerUrlMapper(value="/testJson")
	public Map<String,Object> testJson(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,@JdxControllerMethodParam("jdxjdx")String jdxParam) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "jdxmaoshen");
		map.put("jdxjdx", jdxParam);
		return map;
	}
}