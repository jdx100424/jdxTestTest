package com.maoshen.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.maoshen.component.JdxComponent;
import com.maoshen.component.dto.ResponseResultDto;
import com.maoshen.filter.code.ErrorCode;
import com.maoshen.component.controller.json.JsonpUtil;
import com.maoshen.component.controller.mapper.ControllerActionMethod;
import com.maoshen.component.controller.mapper.ControllerActionMethodParam;

public class JdxFilter implements Filter {
	private static final String SERVLET_REQUEST_NAME = "javax.servlet.ServletRequest";
	private static final String SERVLET_RESPONSE_NAME = "javax.servlet.ServletResponse";
	private static final String APPLICATION_JSON = "application/json";

	public void init(FilterConfig filterConfig) throws ServletException {
		// 注入controller
		try {
			JdxComponent.getJdxComponent().init(filterConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		javax.servlet.http.HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest) request;
		javax.servlet.http.HttpServletResponse httpServletResponse = (javax.servlet.http.HttpServletResponse) response;

		// 获取body信息，用于组装标记为JSON的参数
		String body = charBodyReader(httpServletRequest);

		httpServletResponse.setContentType("application/json; charset=utf-8");
		httpServletResponse.setCharacterEncoding("UTF-8");
		String target = httpServletRequest.getRequestURI();

		ControllerActionMethod controllerActionMethod = JdxComponent.getJdxComponent().getControllerMapping(target);

		Object resultObject = null;
		ResponseResultDto<Object> result = new ResponseResultDto<Object>();
		if (controllerActionMethod == null || StringUtils.isBlank(controllerActionMethod.getMethodName())) {
			// controller映射找不到，直接404
			result.setCode(ErrorCode.HTTP_NOT_FOUNT.getCode());
		} else {
			try {
				boolean isJson = false;
				if (APPLICATION_JSON.equals(httpServletRequest.getContentType())) {
					isJson = true;
				}

				ControllerActionMethodParam[] classParam = controllerActionMethod.getControllerActionMethodParam();

				if (classParam == null || classParam.length == 0) {
					resultObject = doNoParam(controllerActionMethod);
				} else {
					resultObject = doHasParam(httpServletRequest, httpServletResponse, body, controllerActionMethod,
							isJson, classParam);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.setCode(ErrorCode.SYSTEM_ERROR.getCode());
			}
		}

		result.setData(resultObject);
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();
			out.write(JsonpUtil.restJsonp(request.getParameter("callback"), result));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		chain.doFilter(httpServletRequest, response);
	}

	/**
	 * 有参数方法反射调用
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param body
	 * @param controllerActionMethod
	 * @param isJson
	 * @param classParam
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object doHasParam(javax.servlet.http.HttpServletRequest httpServletRequest,
			javax.servlet.http.HttpServletResponse httpServletResponse, String body,
			ControllerActionMethod controllerActionMethod, boolean isJson, ControllerActionMethodParam[] classParam)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object resultObject;
		//调用方法传入的参数值
		Object[] ObjectArr = new Object[classParam.length];
		//调用方法本身设置的参数信息
		Class<?>[] classObject = new Class[classParam.length];

		int i = 0;
		for (ControllerActionMethodParam cls : classParam) {
			classObject[i] = cls.getParam();
			// 查询是否为request or response
			boolean isRequestOrResponse = false;
			for (Class<?> inter : cls.getParam().getInterfaces()) {
				if (SERVLET_REQUEST_NAME.equals(inter.getName())) {
					ObjectArr[i] = httpServletRequest;
					isRequestOrResponse = true;
					break;
				} else if (SERVLET_RESPONSE_NAME.equals(inter.getName())) {
					ObjectArr[i] = httpServletResponse;
					isRequestOrResponse = true;
					break;
				}
			}
			if (isRequestOrResponse == false) {
				if (cls.getJdxControllerJson() != null && isJson) {
					// json
					try {
						Object resultJsonObj = JSONObject.parseObject(body, cls.getParam());
						ObjectArr[i] = resultJsonObj;
					} catch (Exception e) {
						ObjectArr[i] = null;
					}

				} else if (cls.getJdxControllerMethodParam() != null
						&& StringUtils.isNotBlank(cls.getJdxControllerMethodParam().value())) {

					String[] strArr = httpServletRequest.getParameterMap()
							.get(cls.getJdxControllerMethodParam().value());
					if (strArr != null && strArr.length > 0) {
						// 对java.lang里面数值类型的类进行扫描
						ObjectArr[i] = matchParamValue(i, cls, strArr);
					} else {
						ObjectArr[i] = null;
					}

				} else {
					ObjectArr[i] = null;
				}
			}
			i++;
		}

		resultObject = controllerActionMethod.getActionObj().getClass()
				.getMethod(controllerActionMethod.getMethodName(), classObject)
				.invoke(controllerActionMethod.getActionObj(), ObjectArr);
		return resultObject;
	}

	/**
	 * 无参数方法反射调用
	 * @param controllerActionMethod
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object doNoParam(ControllerActionMethod controllerActionMethod)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object resultObject;
		//无参数
		resultObject = controllerActionMethod.getActionObj().getClass()
				.getMethod(controllerActionMethod.getMethodName())
				.invoke(controllerActionMethod.getActionObj());
		return resultObject;
	}

	/**
	 * 匹配参数值
	 * 
	 * @param ObjectArr
	 * @param i
	 * @param cls
	 * @param strArr
	 */
	private Object matchParamValue(int i, ControllerActionMethodParam cls, String[] strArr) {
		try {
			if (Class.forName(cls.getParam().getName()).newInstance() instanceof java.lang.String) {
				return strArr[0];
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(int.class).newInstance(1) instanceof java.lang.Integer) {
				return Integer.parseInt(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(long.class).newInstance(1L) instanceof java.lang.Long) {
				return Long.parseLong(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(double.class).newInstance(1D) instanceof java.lang.Double) {
				return Double.parseDouble(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(float.class).newInstance(1F) instanceof java.lang.Float) {
				return Float.parseFloat(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(boolean.class).newInstance(true) instanceof java.lang.Boolean) {
				return Boolean.parseBoolean(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(byte.class).newInstance(true) instanceof java.lang.Byte) {
				return Byte.parseByte(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(short.class).newInstance(true) instanceof java.lang.Short) {
				return Short.parseShort(strArr[0]);
			}
		} catch (Exception e) {

		}

		try {
			if (cls.getParam().getConstructor(char.class).newInstance(true) instanceof java.lang.Character) {
				char[] arr = strArr[0].toCharArray();
				return new Character(arr[0]);
			}
		} catch (Exception e) {

		}
		return null;

	}

	/**
	 * header body读取
	 * 
	 * @param request
	 * @throws IOException
	 */
	private String charBodyReader(HttpServletRequest request) throws IOException {
		BufferedReader br = request.getReader();
		String str = "";
		StringBuilder wholeStr = new StringBuilder();
		while ((str = br.readLine()) != null) {
			wholeStr.append(str);
		}
		return wholeStr.toString();
	}

	public void destroy() {

	}
}
