package com.maoshen.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.maoshen.component.JdxComponent;
import com.maoshen.component.dto.ResponseResultDto;
import com.maoshen.component.controller.json.JsonpUtil;
import com.maoshen.component.controller.mapper.ControllerActionMethod;


public class JdxFilter implements Filter{
	public void init(FilterConfig filterConfig) throws ServletException {
		//注入controller
		try {
			JdxComponent.getJdxComponent().init(filterConfig);
			System.out.println(JdxComponent.getJdxComponent().getJdxControllerMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		javax.servlet.http.HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest)request;
		javax.servlet.http.HttpServletResponse httpServletResponse = (javax.servlet.http.HttpServletResponse)response;

		httpServletResponse.setContentType("application/json; charset=utf-8");
		httpServletResponse.setCharacterEncoding("UTF-8");
		String target = httpServletRequest.getRequestURI();
		
		ControllerActionMethod controllerActionMethod = JdxComponent.getJdxComponent().getControllerMapping(target);
		//UserHttpServletResponseWrapper responseWrapper = new UserHttpServletResponseWrapper(httpServletResponse);  

		Object resultObject = null;
		ResponseResultDto<Object> result = new ResponseResultDto<Object>();
		if(controllerActionMethod==null) {
			result.setCode(404);
		}else {
			try {
				Object obj = controllerActionMethod.getActionObj();
				String methodName = controllerActionMethod.getMethodName();
				if(StringUtils.isBlank(methodName)) {
					result.setCode(404);
				}else {
					resultObject = obj.getClass().getMethod(methodName).invoke(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.setCode(500);
			}
		}

		result.setData(resultObject);
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();
			out.write(JsonpUtil.restJsonp(request.getParameter("callback"), result));
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(out!=null) {
				out.close();
			}
		}
		chain.doFilter(httpServletRequest, response);
	}

	public void destroy() {

	}

}
