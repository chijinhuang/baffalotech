package com.baffalotech.integration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> List<T> getBeansOfType(Class<T> clazz) {
		Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
		List<T> list = new ArrayList<T>();
		beansOfType.forEach((key, value) -> {
			list.add(value);
		});
		return list;
	}

}
