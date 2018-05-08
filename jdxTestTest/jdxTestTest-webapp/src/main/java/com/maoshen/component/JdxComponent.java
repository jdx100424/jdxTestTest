package com.maoshen.component;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.maoshen.component.constant.JdxControllerConstant;
import com.maoshen.component.controller.annotation.JdxController;
import com.maoshen.component.controller.annotation.JdxControllerJson;
import com.maoshen.component.controller.annotation.JdxControllerMethodParam;
import com.maoshen.component.controller.annotation.JdxControllerUrlMapper;
import com.maoshen.component.controller.mapper.ControllerAction;
import com.maoshen.component.controller.mapper.ControllerActionMethod;
import com.maoshen.component.controller.mapper.ControllerActionMethodParam;
import com.maoshen.component.controller.mapper.JdxMapper;

public class JdxComponent {
	private static final JdxComponent jdxComponent = new JdxComponent();
	//本项目扫描路径
	private String jdxControllerPackagePath = null;
	
	//CONTROLLER真正保存的映射URL和实体类
	private JdxMapper jdxControllerMapper = new JdxMapper();

	protected ServletContext servletContext;

	// 第一轮CONTROLLER扫描存储
	private List<ControllerAction> controllerActionList = new ArrayList<ControllerAction>();

	public JdxMapper getJdxControllerMapper() {
		return jdxControllerMapper;
	}

	public static JdxComponent getJdxComponent() {
		return jdxComponent;
	}

	public void init(FilterConfig filterConfig) throws Exception {
		String configClass = filterConfig.getInitParameter("configClass");
		if(StringUtils.isBlank(configClass)) {
			throw new Exception("configClass valus is not allow null");
		}
		jdxControllerPackagePath = configClass;
		servletContext = filterConfig.getServletContext();
		
		// 注入 controller
		injectController();
		// 注入controller里面的url映射
		injectControllerUrlMapping();
	}

	/**
	 * controller 里面的方法的URL映射
	 */
	private void injectControllerUrlMapping() {
		if (controllerActionList != null && controllerActionList.isEmpty() == false) {
			for (ControllerAction controllerAction : controllerActionList) {
				// 对每个CONTROLLER扫描各个方法头顶的URL映射
				Method[] methods = controllerAction.getActionObj().getClass().getMethods();
				if (methods != null && methods.length > 0) {
					for (Method method : methods) {
						JdxControllerUrlMapper jdxControllerUrlMapper = method
								.getAnnotation(JdxControllerUrlMapper.class);
						if (jdxControllerUrlMapper != null) {
							String value = jdxControllerUrlMapper.value();
							value = dealSlash(value);
							ControllerActionMethod controllerActionMethod = new ControllerActionMethod();
							controllerActionMethod.setActionObj(controllerAction.getActionObj());
							controllerActionMethod.setMethodName(method.getName());
							Parameter[] paramArr = method.getParameters();
							
							int i = 0;
							//每个方法属性信息
							ControllerActionMethodParam[] controllerActionMethodParamArr = new ControllerActionMethodParam[paramArr.length];
							for(Parameter p :paramArr) {
								ControllerActionMethodParam controllerActionMethodParam = new ControllerActionMethodParam();
								controllerActionMethodParam.setParam(p.getType());
								//强制参数名
								JdxControllerMethodParam jdxControllerMethodParam = p.getAnnotation(JdxControllerMethodParam.class);
								controllerActionMethodParam.setJdxControllerMethodParam(jdxControllerMethodParam);
								//是否为json
								JdxControllerJson jdxControllerJson = p.getAnnotation(JdxControllerJson.class);
								controllerActionMethodParam.setJdxControllerJson(jdxControllerJson);
								
								controllerActionMethodParamArr[i] = controllerActionMethodParam;
								i++;
							}
							controllerActionMethod.setControllerActionMethodParam(controllerActionMethodParamArr);
							
							
							jdxControllerMapper.getMap().put(controllerAction.getUrlMapperStr() + value,
									controllerActionMethod);
						}
					}
				}

			}
		}
		System.out.println("Controller真正保存的映射URL和实体类:"+JSONObject.toJSONString(jdxControllerMapper));
	}

	/**
	 * controller对象注入
	 * 
	 * @throws Exception
	 */
	private void injectController() throws Exception {
		String classpath = JdxComponent.class.getResource("/").getPath();
		String pa = jdxControllerPackagePath.replace(JdxControllerConstant.PACKAGE_SEPARATOR,
				JdxControllerConstant.PATH_SEPARATOR);
		String searchPath = classpath + pa;
		Set<String> allClassSet = JdxMapper.doPath(new File(searchPath));

		if (allClassSet != null && allClassSet.isEmpty() == false) {
			for (String str : allClassSet) {
				str = str.replace(classpath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\", ".")
						.replace(".class", "");
				try {
					Class<?> cls = Class.forName(str);
					// 不允许为抽象类或者接口
					if (Modifier.isAbstract(cls.getModifiers()) == false && cls.isInterface() == false) {
						JdxController jdxController = cls.getAnnotation(JdxController.class);
						if (jdxController != null) {
							String value = jdxController.value();
							if (StringUtils.isBlank(value)) {
								String arr[] = cls.getName().split("\\.");
								if (arr != null && arr.length > 0) {
									String s = arr[arr.length - 1];
									value = s.substring(0, 1).toLowerCase() + s.substring(1);
								}
							}
							try {
								Object newInstance = cls.newInstance();
								ControllerAction controllerAction = new ControllerAction();
								value = dealSlash(value);
								controllerAction.setUrlMapperStr(value);
								controllerAction.setActionObj(newInstance);
								controllerActionList.add(controllerAction);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} 
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("第一轮Controller扫描存储:"+JSONObject.toJSONString(controllerActionList));
	}

	/**
	 * 根据访问的URL获取对应的映射类
	 * @param target
	 * @return
	 */
	public ControllerActionMethod getControllerMapping(String target) {
		target = dealSlash(target);
		ControllerActionMethod controllerActionMethod = jdxControllerMapper.getMap().get(target);
		if (controllerActionMethod != null) {
			return controllerActionMethod;
		}
		return null;
	}
	
	/**
	 * URL映射斜杠的问题处理
	 * @param value
	 * @return
	 */
	private String dealSlash(String value) {
		if(StringUtils.isBlank(value)) {
			return null;
		}
		if(value.substring(0, 1).indexOf("/")>-1==false) {
			value = "/" + value;
		}
		if(value.substring(value.length()-1, value.length()).indexOf("/")>-1==true) {
			value = value.substring(0,value.length()-1);
		}
		return value;
	}
}
