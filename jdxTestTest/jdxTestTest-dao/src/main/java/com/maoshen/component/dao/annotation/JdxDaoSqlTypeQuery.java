package com.maoshen.component.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记为查询的SQL
 * 
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxDaoSqlTypeQuery {

}
