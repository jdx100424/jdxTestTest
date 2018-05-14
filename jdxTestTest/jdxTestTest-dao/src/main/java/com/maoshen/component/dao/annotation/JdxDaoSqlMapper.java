package com.maoshen.component.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * SQL语句
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxDaoSqlMapper {
	String value() default "";
}
