package com.baffalotech.integration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Connector {

	/**
	 * connector 的名字,支持通配，暂时只支持*,适用于所有的connector
	 * @return
	 */
	String name();
	
	/**
	 *	优先级， 数值越小越前执行
	 * @return
	 */
	int order() default 0;
}
