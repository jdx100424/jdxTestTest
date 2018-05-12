package com.maoshen.jdxtest.controller;

import java.util.HashMap;
import java.util.Map;

import com.maoshen.boot.service.JdxTestService;
import com.maoshen.component.controller.annotation.JdxController;
import com.maoshen.component.controller.annotation.JdxControllerJson;
import com.maoshen.component.controller.annotation.JdxControllerMethodParam;
import com.maoshen.component.controller.annotation.JdxControllerUrlMapper;
import com.maoshen.component.service.annotation.JdxServiceInject;
import com.maoshen.jdxtest.controller.vo.request.JdxRequest;

@JdxController(value = "jdxGoController/1/")
public class JdxGoController {
	
	@JdxServiceInject("jdxTestService")
	public JdxTestService jdxTestService;
	
	@JdxControllerUrlMapper(value="testGo/")
	public Map<String,Object> testGo(String s,@JdxControllerMethodParam("jdxjdx")String jdxParam,Integer i,@JdxControllerMethodParam("jdxjdxInt")Integer jdxjdxInt) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "testGo");
		map.put("3", jdxParam);
		map.put("4", jdxjdxInt);
		
		map.put("-1", "?:"+s);
		map.put("-2","?:"+i);
		
		map.put("jdxTestService", jdxTestService.sayHello("Map<String,Object>"));
		return map;
	}
	
	/**
	 * http://localhost:8080/jdxGoController/1/testGoJson?jdxjdx=ddddd&jdxjdxInt=4
	 * Content-Type,application/json
	 * {
			"id":1,
			"name":"maoshen"
		}
	 * @param jdxParam
	 * @param jdxjdxInt
	 * @param jdxRequest
	 * @return
	 */
	@JdxControllerUrlMapper(value="testGoJson/")
	public Map<String,Object> testGoJson(@JdxControllerMethodParam("jdxjdx")String jdxParam,@JdxControllerMethodParam("jdxjdxInt")Integer jdxjdxInt,@JdxControllerJson JdxRequest jdxRequest) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", System.currentTimeMillis());
		map.put("2", "testGo");
		map.put("3", jdxParam);
		map.put("4", jdxjdxInt);
		map.put("id", jdxRequest.getId());
		map.put("name", jdxRequest.getName());
		return map;
	}
}