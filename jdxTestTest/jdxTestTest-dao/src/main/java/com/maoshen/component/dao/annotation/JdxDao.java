package com.maoshen.component.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记为controller类
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxDao {
	String value() default "";
}
