package com.baffalotech.integration.http.netty.servlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletSecurityElement;

import org.apache.commons.lang3.StringUtils;

import com.baffalotech.integration.http.netty.servlet.util.UrlMapper;

import java.util.*;

/**
 * servlet注册
 * 
 * @author acer01 2018/7/14/014
 */
public class NettyServletRegistration
		implements javax.servlet.ServletRegistration, javax.servlet.ServletRegistration.Dynamic {

	private String servletName;
	private Servlet servlet;
	private ServletConfig servletConfig;
	private NettyServletContext servletContext;
	private Map<String, String> initParameterMap;
	private NettyServletRegistration self;
	private Set<String> mappingSet;
	private Boolean asyncSupported;
	private UrlMapper<NettyServletRegistration> urlMapper;
	private MultipartConfigElement multipartConfigElement;
	private ServletSecurityElement servletSecurityElement;

	public NettyServletRegistration(String servletName, Servlet servlet, NettyServletContext servletContext,
			UrlMapper<NettyServletRegistration> urlMapper) {
		this.servletName = servletName;
		this.servlet = servlet;
		this.servletContext = servletContext;
		this.urlMapper = urlMapper;
		this.initParameterMap = new HashMap<>();
		this.mappingSet = new HashSet<>();
		this.self = this;

		this.servletConfig = new ServletConfig() {
			@Override
			public String getServletName() {
				return self.servletName;
			}

			@Override
			public javax.servlet.ServletContext getServletContext() {
				return self.servletContext;
			}

			@Override
			public String getInitParameter(String name) {
				return self.getInitParameter(name);
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				return Collections.enumeration(self.getInitParameters().keySet());
			}
		};
	}

	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public Boolean isAsyncSupported() {
		return asyncSupported;
	}

	@Override
	public Set<String> addMapping(String... urlPatterns) {
		mappingSet.addAll(Arrays.asList(urlPatterns));
		for (String pattern : urlPatterns) {
			if (urlMapper != null) {
				if(servlet instanceof NettyDefaultHttpServlet)
				{
					urlMapper.addMapping(pattern, this, servletName);
				}
				if (StringUtils.equals(servletContext.getServletContextName(), "default")
						&& !(servlet instanceof NettyProxyServlet)) {
					urlMapper.addMapping(pattern, this, servletName);
				}
				if (servlet instanceof NettyProxyServlet
						&& !StringUtils.equals(servletContext.getServletContextName(), "default")) {
					urlMapper.addMapping(pattern, this, servletName);
				}

			}
		}
		return mappingSet;
	}

	@Override
	public Collection<String> getMappings() {
		return mappingSet;
	}

	@Override
	public String getRunAsRole() {
		return null;
	}

	@Override
	public String getName() {
		return servletName;
	}

	@Override
	public String getClassName() {
		return servlet.getClass().getName();
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		return initParameterMap.put(name, value) != null;
	}

	@Override
	public String getInitParameter(String name) {
		return initParameterMap.get(name);
	}

	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		this.initParameterMap = initParameters;
		return initParameterMap.keySet();
	}

	@Override
	public Map<String, String> getInitParameters() {
		return initParameterMap;
	}

	// ==============

	@Override
	public void setLoadOnStartup(int loadOnStartup) {

	}

	@Override
	public Set<String> setServletSecurity(ServletSecurityElement constraint) {
		this.servletSecurityElement = constraint;
		return new HashSet<>(servletSecurityElement.getMethodNames());
	}

	@Override
	public void setMultipartConfig(MultipartConfigElement multipartConfig) {
		this.multipartConfigElement = multipartConfig;
	}

	@Override
	public void setRunAsRole(String roleName) {

	}

	@Override
	public void setAsyncSupported(boolean isAsyncSupported) {
		this.asyncSupported = isAsyncSupported;
	}
}
