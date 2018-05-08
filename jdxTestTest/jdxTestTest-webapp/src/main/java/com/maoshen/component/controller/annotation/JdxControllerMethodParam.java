package com.maoshen.component.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * controller类方法，强制映射request.getparamter的KEY
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.PARAMETER })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxControllerMethodParam {
	String value() default "";
}
