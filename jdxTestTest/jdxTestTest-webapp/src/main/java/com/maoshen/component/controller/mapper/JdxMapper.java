package com.maoshen.component.controller.mapper;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * URL映射的对象信息，包括方法名和运行的controller对象
 * @author dell
 *
 */
public class JdxMapper {
	private Map<String,ControllerActionMethod> map = new HashMap<String,ControllerActionMethod>();
	
	public Map<String,ControllerActionMethod> getMap(){
		return map;
	}
	
	public static Set<String> doPath(File file) {
		Set<String> classPaths = new HashSet<String>();
		if (file.isDirectory()) {// 文件夹
			// 文件夹我们就递归
			File[] files = file.listFiles();
			for (File f1 : files) {
				classPaths.addAll(doPath(f1));
			}
		} else {// 标准文件
			// 标准文件我们就判断是否是class文件
			if (file.getName().endsWith(".class")) {
				// 如果是class文件我们就放入我们的集合中。
				classPaths.add(file.getPath());
			}
		}
		return classPaths;
	}
}
