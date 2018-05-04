package com.maoshen.component.controller;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * controller URL和方法的映射
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxControllerUrlMapper {
	String value() default "";
}
