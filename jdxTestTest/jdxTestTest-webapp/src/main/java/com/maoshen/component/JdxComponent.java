package com.maoshen.component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
import com.maoshen.component.service.annotation.JdxService;
import com.maoshen.component.service.annotation.JdxServiceInject;
import com.maoshen.component.service.mapper.ServiceAction;

public class JdxComponent {
	private static final JdxComponent jdxComponent = new JdxComponent();
	//本项目扫描路径
	private String jdxControllerPackagePath = null;
	
	//CONTROLLER真正保存的映射URL和实体类
	private JdxMapper<ControllerActionMethod> jdxControllerMapper = new JdxMapper<ControllerActionMethod>();

	protected ServletContext servletContext;

	// 第一轮CONTROLLER扫描存储
	private List<ControllerAction> controllerActionList = new ArrayList<ControllerAction>();
	// 第二轮service扫描存储
	private Map<String,ServiceAction> serviceActionMap = new HashMap<String,ServiceAction>();

	
	public JdxMapper<ControllerActionMethod> getJdxControllerMapper() {
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
		
		// 注入 service
		injectService();
		
		// 每个service里面的子service
		injectServiceField();
		
		// 注入controller里面的url映射
		injectControllerUrlMapping();
		
		// 注入controller里面的service
		injectControllerinjectServiceField();
	}

	/**
	 * 注入controller里面的service
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void injectControllerinjectServiceField() throws Exception {
		if(jdxControllerMapper!=null && jdxControllerMapper.getMap()!=null && jdxControllerMapper.getMap().isEmpty()==false) {
			Iterator<Entry<String, ControllerActionMethod>> it = jdxControllerMapper.getMap().entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, ControllerActionMethod> e = it.next();
				Field[] arr = e.getValue().getActionObj().getClass().getFields();
				if(arr!=null && arr.length>0) {
					for(Field f:arr) {
						JdxServiceInject jdxServiceInject = f.getAnnotation(JdxServiceInject.class);
        				if(jdxServiceInject!=null) {
        					ServiceAction sa = serviceActionMap.get(jdxServiceInject.value());
        					if(sa!=null) {
        						f.set(e.getValue().getActionObj(), sa.getActionObj());
        					}
        					System.out.println();
        				}
					}
				}
			}
		}
		System.out.println("Controller第3轮，保存里面的service:"+JSONObject.toJSONString(jdxControllerMapper));
	}

	/**
	 * 每个service里面的2级或者2级以上的子service
	 * @throws Exception 
	 */
	private void injectServiceField() throws Exception {
        if(serviceActionMap!=null && serviceActionMap.isEmpty()==false) {
        	Iterator<Entry<String, ServiceAction>> it = serviceActionMap.entrySet().iterator();
        	while(it.hasNext()) {
        		Entry<String, ServiceAction> e = it.next();
        		ServiceAction serviceAction = e.getValue();
        		Object obj = serviceAction.getActionObj();
        		if(serviceAction.getFieldArr()!=null && serviceAction.getFieldArr().length>0) {
        			for(Field f:serviceAction.getFieldArr()) {
        				JdxServiceInject jdxServiceInject = f.getAnnotation(JdxServiceInject.class);
        				if(jdxServiceInject!=null) {
        					ServiceAction sa = serviceActionMap.get(jdxServiceInject.value());
        					if(sa!=null) {
        						Field oriField = obj.getClass().getField(f.getName());
        						oriField.set(obj, sa.getActionObj());
        					}
        				}
        			}
        		}
        	}
        }
		System.out.println("第2轮service扫描存储:"+JSONObject.toJSONString(serviceActionMap));
	}

	/**
	 * service
	 * @throws Exception 
	 */
	private void injectService() throws Exception {
		//通过当前线程得到类加载器从而得到URL的枚举
        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(jdxControllerPackagePath.replace(".", "/"));
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();//得到的结果大概是：jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit
            String protocol = url.getProtocol();//大概是jar
            if ("jar".equalsIgnoreCase(protocol)) {
                //转换为JarURLConnection
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                if (connection != null) {
                    JarFile jarFile = connection.getJarFile();
                    if (jarFile != null) {
                        //得到该jar文件下面的类实体
                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                        while (jarEntryEnumeration.hasMoreElements()) {
                            /*entry的结果大概是这样：
                                    org/
                                    org/junit/
                                    org/junit/rules/
                                    org/junit/runners/*/
                            JarEntry entry = jarEntryEnumeration.nextElement();
                            String jarEntryName = entry.getName();
                            //这里我们需要过滤不是class文件和不在basePack包名下的类
                            if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/",".").startsWith(jdxControllerPackagePath)) {
                            	try {
	                            	String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
	                                Class<?> cls = Class.forName(className);
	                                // 不允许为抽象类或者接口
	            					if (Modifier.isAbstract(cls.getModifiers()) == false && cls.isInterface() == false) {
	            						JdxService jdxService = cls.getAnnotation(JdxService.class);
	            						if (jdxService != null) {
	            							String value = jdxService.value();
	            							if (StringUtils.isBlank(value)) {
	            								String arr[] = cls.getName().split("\\.");
	            								if (arr != null && arr.length > 0) {
	            									String s = arr[arr.length - 1];
	            									value = s.substring(0, 1).toLowerCase() + s.substring(1);
	            								}
	            							}
	            							try {
	            								Object newInstance = cls.newInstance();
	            								ServiceAction serviceAction = new ServiceAction();
	            								serviceAction.setActionObj(newInstance);
	            								serviceAction.setValue(value);
	            								//参数,目前先强制扫描public属性的
	            								Field[] fields = cls.getFields();
	
	            								serviceAction.setParamObjArr(null);
	            								serviceAction.setFieldArr(fields);
	            								serviceActionMap.put(value, serviceAction);
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
                    }
                }
            }
        }
		System.out.println("第1轮service扫描存储:"+JSONObject.toJSONString(serviceActionMap));
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
		System.out.println("Controller第2轮，真正保存的映射URL和实体类:"+JSONObject.toJSONString(jdxControllerMapper));
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
