package com.baffalotech.integration.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 控制器的标注
 * 
 * @author chijinhuang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface IController {

	/**
	 * connector的名称
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 用于区分对象的那个字段的值作为区分接口的字段
	 * 
	 * @return
	 */
	String discriminator() default "";
}
