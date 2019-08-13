package com.baffalotech.integration.mvc.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import com.baffalotech.integration.annotation.Connector;
import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.HandlerMethod;
import com.baffalotech.integration.mvc.HandlerMethodMapping;
import com.baffalotech.integration.mvc.IContext;
import com.baffalotech.integration.mvc.IFilter;
import com.baffalotech.integration.mvc.IFilterChain;
import com.baffalotech.integration.mvc.MessageConverter;
import com.baffalotech.integration.mvc.annotation.IController;
import com.baffalotech.integration.util.ApplicationContextUtil;

/**
 * 
 * @author chijinhuang
 *
 */
public class RequestMappingHandlerMapping extends WebApplicationObjectSupport implements HandlerMethodMapping{
	
	/**
	 * Bean name prefix for target beans behind scoped proxies. Used to exclude those
	 * targets from handler method detection, in favor of the corresponding proxies.
	 * <p>We're not checking the autowire-candidate status here, which is how the
	 * proxy target filtering problem is being handled at the autowiring level,
	 * since autowire-candidate may have been turned to {@code false} for other
	 * reasons, while still expecting the bean to be eligible for handler methods.
	 * <p>Originally defined in {@link org.springframework.aop.scope.ScopedProxyUtils}
	 * but duplicated here to avoid a hard dependency on the spring-aop module.
	 */
	private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";
	
	private MappingRegistry registry = new MappingRegistry();

	@Override
	public HandlerMethod getHandlerMethod(String name, String value) {
		// TODO Auto-generated method stub
		return registry.lookupHandlerMethod(name, value);
	}
	
	@Override
	public HandlerMethod getHandlerMethod(String name,Object value) {
		Object bean = registry.lookupBean(name);
		if(bean != null)
		{
			IController controller = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), IController.class);
			if(controller != null)
			{
				String discriminator = controller.discriminator();
				//get discriminator property value by commons beanutils library
				try {
					Object discriminatorValue = PropertyUtils.getProperty(value, discriminator);
					//convert discriminator value to String
					return getHandlerMethod(name, discriminatorValue.toString());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return null;
	}
	
	@Override
	public String getDiscriminator(String name) {
		// TODO Auto-generated method stub
		Object bean = registry.lookupBean(name);
		if(bean != null)
		{
			IController controller = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), IController.class);
			if(controller != null)
			{
				return controller.discriminator();
			}
		}
		return null;
	}
	
	@Override
	protected void initApplicationContext(ApplicationContext context) {
		initHandlerMethods();
	}
	
	/**
	 * Scan beans in the ApplicationContext, detect and register handler methods.
	 * @see #getCandidateBeanNames()
	 * @see #processCandidateBean
	 * @see #handlerMethodsInitialized
	 */
	protected void initHandlerMethods() {
		for (String beanName : getCandidateBeanNames()) {
			if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
				processCandidateBean(beanName);
			}
		}
	}
	
	
	protected String[] getCandidateBeanNames() {
		return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class);
	}
	
	/**
	 * Determine the type of the specified candidate bean and call
	 * {@link #detectHandlerMethods} if identified as a handler type.
	 * <p>This implementation avoids bean creation through checking
	 * {@link org.springframework.beans.factory.BeanFactory#getType}
	 * and calling {@link #detectHandlerMethods} with the bean name.
	 * @param beanName the name of the candidate bean
	 * @since 5.1
	 * @see #isHandler
	 * @see #detectHandlerMethods
	 */
	protected void processCandidateBean(String beanName) {
		Class<?> beanType = null;
		try {
			beanType = obtainApplicationContext().getType(beanName);
		}
		catch (Throwable ex) {
			// An unresolvable bean type, probably from a lazy bean - let's ignore it.
			if (logger.isTraceEnabled()) {
				logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
			}
		}
		if (beanType != null && isHandler(beanType)) {
			detectHandlerMethods(beanName);
		}
	}
	
	/**
	 * Look for handler methods in the specified handler bean.
	 * @param handler either a bean name or an actual handler instance
	 * @see #getMappingForMethod
	 */
	protected void detectHandlerMethods(Object handler) {
		Class<?> handlerType = (handler instanceof String ?
				obtainApplicationContext().getType((String) handler) : handler.getClass());

		if (handlerType != null) {
			Class<?> userType = ClassUtils.getUserClass(handlerType);
			Map<Method, Object> methods = MethodIntrospector.selectMethods(userType,
					(MethodIntrospector.MetadataLookup<Object>) method -> {
						try {
							return handler;
						}
						catch (Throwable ex) {
							throw new IllegalStateException("Invalid mapping on handler class [" +
									userType.getName() + "]: " + method, ex);
						}
					});
			methods.forEach((method, mapping) -> {
				Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
				registry.register(obtainApplicationContext().getBean((String)handler),invocableMethod);
			});
		}
	}
	
	protected boolean isHandler(Class<?> beanType) {
		return AnnotatedElementUtils.hasAnnotation(beanType, IController.class);
	}
	
	class MappingRegistry {
		Map<String, List<HandlerMethod>> lookupMap = new HashMap<String, List<HandlerMethod>>();
		Map<String,Object> beanMap = new HashMap<String, Object>();
		
		HandlerMethod lookupHandlerMethod(String name,String value)
		{
			for(Entry<String, List<HandlerMethod>> entry : lookupMap.entrySet())
			{
				if(entry.getKey().equals(name))
				{
					if(entry.getValue() != null)
					{
						for(HandlerMethod handlerMethod : entry.getValue())
						{
							if(value.equals(handlerMethod.getDiscriminatorValue()))
							{
								return handlerMethod;
							}
						}
					}
				}
			}
			return null;
		}
		
		Object lookupBean(String name)
		{
			return beanMap.get(name);
		}
		
		void register(Object handler,Method method)
		{
			IController controller = AnnotatedElementUtils.findMergedAnnotation(handler.getClass(), IController.class);
			List<HandlerMethod> handlerMethods = lookupMap.get(controller.name());
			if (handlerMethods == null) {
				handlerMethods = new ArrayList<HandlerMethod>();
				lookupMap.put(controller.name(), handlerMethods);
			}
			handlerMethods.add(new HandlerMethod(handler, method));
			beanMap.put(controller.name(),handler);
		}
	}

	@Override
	public IFilterChain getFilterChain(IContext context) {
		// TODO Auto-generated method stub
		DefaultIFilterChain filterChain = new DefaultIFilterChain();
		List<IFilter> filterBeanList = ApplicationContextUtil.getBeansOfType(IFilter.class);
		filterBeanList.forEach(filterBean ->{
			Connector connector = AnnotatedElementUtils.findMergedAnnotation(filterBean.getClass(),Connector.class);
			if(StringUtils.equals(context.getConnectorName(), connector.name()))
			{
				filterChain.addFilter(filterBean);
			}
		});
		Collections.sort(filterChain.getFilterList(), (one,other)->{return one.getOrder()-other.getOrder();});
		filterChain.setHandlerMethodMapping(this);
		List<MessageConverter> messageConverterBeanList = ApplicationContextUtil.getBeansOfType(MessageConverter.class);
		MessageConverter connecterConverter = null;
		for(MessageConverter messageConverter : messageConverterBeanList)
		{
			Connector connector = AnnotatedElementUtils.findMergedAnnotation(messageConverter.getClass(),Connector.class);
			if(connector != null && StringUtils.equals(connector.name(),context.getConnectorName()))
			{
				connecterConverter = messageConverter;
			}
		}
		if(connecterConverter == null)
		{
			switch (context.getContentType()) {
			case XML:
				connecterConverter = ApplicationContextUtil.getBean(DefaultXmlMessageConverter.class);
				break;

			default:
				connecterConverter = ApplicationContextUtil.getBean(DefaultJsonMessageConverter.class);
				break;
			}
		}
		filterChain.setMessageConverter(connecterConverter);
		return filterChain;
	}

}
