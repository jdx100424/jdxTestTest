package com.maoshen.component.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * controller类里面，参数标记为JSON
 * @author dell
 *
 */
@Target({ java.lang.annotation.ElementType.PARAMETER })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface JdxControllerJson {

}
