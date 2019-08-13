package com.baffalotech.integration.http.netty.servlet;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.core.StandardThreadExecutor;
import com.baffalotech.integration.http.netty.core.constants.HttpConstants;
import com.baffalotech.integration.http.netty.core.util.HostUtil;
import com.baffalotech.integration.http.netty.core.util.SystemPropertyUtil;
import com.baffalotech.integration.http.netty.core.util.TypeCastUtil;
import com.baffalotech.integration.http.netty.servlet.pathmap.MappedResource;
import com.baffalotech.integration.http.netty.servlet.support.NettyServletErrorPageManager;
import com.baffalotech.integration.http.netty.servlet.support.ResourceManager;
import com.baffalotech.integration.http.netty.servlet.support.ServletEventListenerManager;
import com.baffalotech.integration.http.netty.servlet.util.MimeMappingsX;
import com.baffalotech.integration.http.netty.servlet.util.ServletUtil;
import com.baffalotech.integration.http.netty.servlet.util.UrlMapper;
import com.baffalotech.integration.http.netty.session.SessionService;

/**
 * servlet上下文 (生命周期同服务器)
 * @author acer01
 *  2018/7/14/014
 */
public class NettyServletContext implements javax.servlet.ServletContext {

    private static final Logger logger = LoggerFactory.getLogger(NettyServletContext.class);
    /**
     * 默认20分钟,
     */
    private int sessionTimeout = 1200;
    private Map<String,Object> attributeMap = new HashMap<>(16);
    private Map<String,String> initParamMap = new HashMap<>(16);
    private Map<String,NettyServletRegistration> servletRegistrationMap = new HashMap<>(8);
    private Map<String,NettyServletFilterRegistration> filterRegistrationMap = new HashMap<>(8);
    private Set<SessionTrackingMode> defaultSessionTrackingModeSet = new HashSet<>(Arrays.asList(SessionTrackingMode.COOKIE,SessionTrackingMode.URL));

//    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private NettyServletErrorPageManager servletErrorPageManager = new NettyServletErrorPageManager();
    private MimeMappingsX mimeMappings = new MimeMappingsX();
    private ServletEventListenerManager servletEventListenerManager = new ServletEventListenerManager();
    private NettyServletSessionCookieConfig sessionCookieConfig = new NettyServletSessionCookieConfig();
    private UrlMapper<NettyServletRegistration> servletUrlMapper = new UrlMapper<NettyServletRegistration>(true);
    private UrlMapper<NettyServletFilterRegistration> filterUrlMapper = new UrlMapper<NettyServletFilterRegistration>(false);

    private ResourceManager resourceManager;
    private ExecutorService asyncExecutorService;
    private SessionService sessionService;
    private Set<SessionTrackingMode> sessionTrackingModeSet;

    private String serverHeader;
    private String contextPath;
    private String requestCharacterEncoding;
    private String responseCharacterEncoding;
    private String servletContextName;
    private InetSocketAddress servletServerAddress;

    public NettyServletContext(InetSocketAddress socketAddress,ClassLoader classLoader,String docBase) {
        this.servletServerAddress = Objects.requireNonNull(socketAddress);
        String workspace = '/' + (HostUtil.isLocalhost(socketAddress.getHostName())? "localhost":socketAddress.getHostName());
        this.resourceManager = new ResourceManager(docBase,workspace,classLoader);
        this.resourceManager.mkdirs("/");
    }

    public ExecutorService getAsyncExecutorService() {
        if(asyncExecutorService == null) {
            synchronized (this){
                if(asyncExecutorService == null) {
                    asyncExecutorService = new StandardThreadExecutor(8,8);
                }
            }
        }
        return asyncExecutorService;
    }

    public MimeMappingsX getMimeMappings() {
        return mimeMappings;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public NettyServletErrorPageManager getErrorPageManager() {
        return servletErrorPageManager;
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public void setServerHeader(String serverHeader) {
        this.serverHeader = serverHeader;
    }

    public String getServerHeader() {
        return serverHeader;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public ServletEventListenerManager getServletEventListenerManager() {
        return servletEventListenerManager;
    }

    public long getAsyncTimeout(){
        String value = getInitParameter("asyncTimeout");
        if(value == null){
            return 10000;
        }
        try {
            return Long.parseLong(value);
        }catch (NumberFormatException e){
            return 10000;
        }
    }

    public InetSocketAddress getServletServerAddress() {
        return servletServerAddress;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        if(sessionTimeout <= 0){
            return;
        }
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public NettyServletContext getContext(String uripath) {
        return this;
    }

    @Override
    public int getMajorVersion() {
        return 3;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 3;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        if (file == null) {
            return null;
        }
        int period = file.lastIndexOf('.');
        if (period < 0) {
            return null;
        }
        String extension = file.substring(period + 1);
        if (extension.length() < 1) {
            return null;
        }
        return mimeMappings.get(extension);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return resourceManager.getResourcePaths(path);
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return resourceManager.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return resourceManager.getResourceAsStream(path);
    }

    @Override
    public String getRealPath(String path) {
        return resourceManager.getRealPath(path);
    }

    @Override
    public NettyServletRequestDispatcher getRequestDispatcher(String path) {
    	MappedResource<NettyServletRegistration> servletRegistrationResource = servletUrlMapper.getMappingObjectByUri(path);
        
        if(servletRegistrationResource == null){
            return null;
        }
        NettyServletRegistration servletRegistration = servletRegistrationResource.getResource();
        NettyServletFilterChain filterChain = NettyServletFilterChain.newInstance(this,servletRegistration);
        filterUrlMapper.getMappingObjectsByUri(path,filterChain.getFilterRegistrationList());

        NettyServletRequestDispatcher dispatcher = NettyServletRequestDispatcher.newInstance(filterChain);
        dispatcher.setPath(path);
        return dispatcher;
    }

    @Override
    public NettyServletRequestDispatcher getNamedDispatcher(String name) {
        NettyServletRegistration servletRegistration = null == name ? null : getServletRegistration(name);
        if (servletRegistration == null) {
            return null;
        }

        NettyServletFilterChain filterChain = NettyServletFilterChain.newInstance(this,servletRegistration);
        List<NettyServletFilterRegistration> filterList = filterChain.getFilterRegistrationList();
        for (NettyServletFilterRegistration registration : filterRegistrationMap.values()) {
            for(String servletName : registration.getServletNameMappings()){
                if(servletName.equals(name)){
                    filterList.add(registration);
                }
            }
        }

        NettyServletRequestDispatcher dispatcher = NettyServletRequestDispatcher.newInstance(filterChain);
        dispatcher.setName(name);
        return dispatcher;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        NettyServletRegistration registration = servletRegistrationMap.get(name);
        if(registration == null){
            return null;
        }
        return registration.getServlet();
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        List<Servlet> list = new ArrayList<>();
        for(NettyServletRegistration registration : servletRegistrationMap.values()){
            list.add(registration.getServlet());
        }
        return Collections.enumeration(list);
    }

    @Override
    public Enumeration<String> getServletNames() {
        List<String> list = new ArrayList<>();
        for(NettyServletRegistration registration : servletRegistrationMap.values()){
            list.add(registration.getName());
        }
        return Collections.enumeration(list);
    }

    @Override
    public void log(String msg) {
        logger.debug(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        logger.debug(msg,exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        logger.debug(message,throwable);
    }

    @Override
    public String getServerInfo() {
        return ServletUtil.getServerInfo()
                .concat("(JDK ")
                .concat(ServletUtil.getJvmVersion())
                .concat(";")
                .concat(ServletUtil.getOsName())
                .concat(" ")
                .concat(ServletUtil.getArch())
                .concat(")");
    }

    @Override
    public String getInitParameter(String name) {
        return initParamMap.get(name);
    }

    public <T>T getInitParameter(String name,T def) {
        String value = getInitParameter(name);
        if(value == null){
            return def;
        }
        Class<?> clazz = def.getClass();
        Object valCast = TypeCastUtil.cast((Object) value,clazz);
        if(valCast != null && valCast.getClass().isAssignableFrom(clazz)){
            return (T) valCast;
        }
        return def;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParamMap.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParamMap.putIfAbsent(name,value) == null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributeMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributeMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        Objects.requireNonNull(name);
        if(object == null){
            removeAttribute(name);
            return;
        }

        Object oldObject = attributeMap.put(name,object);
        ServletEventListenerManager listenerManager = getServletEventListenerManager();
        if(listenerManager.hasServletContextAttributeListener()){
            listenerManager.onServletContextAttributeAdded(new ServletContextAttributeEvent(this,name,object));
            if(oldObject != null){
                listenerManager.onServletContextAttributeReplaced(new ServletContextAttributeEvent(this,name,oldObject));
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        Object oldObject = attributeMap.remove(name);
        ServletEventListenerManager listenerManager = getServletEventListenerManager();
        if(listenerManager.hasServletContextAttributeListener()){
            listenerManager.onServletContextAttributeRemoved(new ServletContextAttributeEvent(this,name,oldObject));
        }
    }

    @Override
    public String getServletContextName() {
        return servletContextName;
    }

    @Override
    public NettyServletRegistration addServlet(String servletName, String className) {
        try {
            return addServlet(servletName, (Class<? extends Servlet>) Class.forName(className).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NettyServletRegistration addServlet(String servletName, Servlet servlet) {
        Servlet newServlet = servletEventListenerManager.onServletAdded(servlet);

        NettyServletRegistration servletRegistration = null;
        if(newServlet == null){
            servletRegistration = new NettyServletRegistration(servletName,servlet,this,servletUrlMapper);
        }else {
            servletRegistration = new NettyServletRegistration(servletName,newServlet,this,servletUrlMapper);
        }
        
        if(servletRegistration != null)
        {
        	 if(StringUtils.equals(getServletContextName(), "default") && !(servlet instanceof NettyProxyServlet))
        	 {
        		 servletRegistrationMap.put(servletName,servletRegistration);
        	 }
        	 
        	 if(servlet instanceof NettyProxyServlet && !StringUtils.equals(getServletContextName(), "default"))
        	 {
        		 servletRegistrationMap.put(servletName,servletRegistration);
        	 }
        }
        return servletRegistration;
    }

    @Override
    public NettyServletRegistration addServlet(String servletName, Class<? extends Servlet> servletClass) {
        Servlet servlet = null;
        try {
            servlet = servletClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return addServlet(servletName,servlet);
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NettyServletRegistration getServletRegistration(String servletName) {
        return servletRegistrationMap.get(servletName);
    }

    @Override
    public Map<String, NettyServletRegistration> getServletRegistrations() {
        return servletRegistrationMap;
    }

    @Override
    public NettyServletFilterRegistration addFilter(String filterName, String className) {
        try {
            return addFilter(filterName, (Class<? extends Filter>) Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NettyServletFilterRegistration addFilter(String filterName, Filter filter) {
        NettyServletFilterRegistration registration = new NettyServletFilterRegistration(filterName,filter,this,filterUrlMapper);
        if(StringUtils.equals(getServletContextName(), "default") && !(filter instanceof NettyProxyFilter))
        {
        	filterRegistrationMap.put(filterName,registration);
        }
        
        if(!StringUtils.equals(getServletContextName(), "default") && (filter instanceof NettyProxyFilter))
        {
        	filterRegistrationMap.put(filterName,registration);
        }
        return registration;
    }

    @Override
    public NettyServletFilterRegistration addFilter(String filterName, Class<? extends Filter> filterClass) {
        try {
            return addFilter(filterName,filterClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return filterRegistrationMap.get(filterName);
    }

    @Override
    public Map<String, NettyServletFilterRegistration> getFilterRegistrations() {
        return filterRegistrationMap;
    }

    @Override
    public NettyServletSessionCookieConfig getSessionCookieConfig() {
        return sessionCookieConfig;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        sessionTrackingModeSet = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return defaultSessionTrackingModeSet;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if(sessionTrackingModeSet == null){
            return getDefaultSessionTrackingModes();
        }
        return sessionTrackingModeSet;
    }

    @Override
    public void addListener(String className) {
        try {
            addListener((Class<? extends EventListener>) Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> void addListener(T listener) {
        Objects.requireNonNull(listener);

        ServletEventListenerManager listenerManager = getServletEventListenerManager();
        if(listener instanceof ServletContextAttributeListener){
            listenerManager.addServletContextAttributeListener((ServletContextAttributeListener) listener);

        }else if(listener instanceof ServletRequestListener){
            listenerManager.addServletRequestListener((ServletRequestListener) listener);

        }else if(listener instanceof ServletRequestAttributeListener){
            listenerManager.addServletRequestAttributeListener((ServletRequestAttributeListener) listener);

        }else if(listener instanceof HttpSessionIdListener){
            listenerManager.addHttpSessionIdListenerListener((HttpSessionIdListener) listener);

        }else if(listener instanceof HttpSessionAttributeListener){
            listenerManager.addHttpSessionAttributeListener((HttpSessionAttributeListener) listener);

        }else if(listener instanceof HttpSessionListener){
            listenerManager.addHttpSessionListener((HttpSessionListener) listener);

        }else if(listener instanceof ServletContextListener){
            listenerManager.addServletContextListener((ServletContextListener) listener);

        }else {
            throw new IllegalArgumentException("applicationContext.addListener.iae.wrongType"+
                    listener.getClass().getName());
        }
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            addListener(listenerClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return resourceManager.getClassLoader();
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return ServletUtil.getServerInfo()
        .concat(" (")
        .concat(servletServerAddress.getHostName())
        .concat(":")
        .concat(SystemPropertyUtil.get("user.name"))
        .concat(")");
    }

    @Override
    public String getRequestCharacterEncoding() {
        if(requestCharacterEncoding == null){
            return HttpConstants.DEFAULT_CHARSET.name();
        }
        return requestCharacterEncoding;
    }

    @Override
    public void setRequestCharacterEncoding(String requestCharacterEncoding) {
        this.requestCharacterEncoding = requestCharacterEncoding;
    }

    @Override
    public String getResponseCharacterEncoding() {
        if(responseCharacterEncoding == null){
            return HttpConstants.DEFAULT_CHARSET.name();
        }
        return responseCharacterEncoding;
    }

    @Override
    public void setResponseCharacterEncoding(String responseCharacterEncoding) {
        this.responseCharacterEncoding = responseCharacterEncoding;
    }

    @Override
    public javax.servlet.ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        // TODO: 2018/11/11/011  addJspFile
        return null;
    }
    
    public MappedResource<NettyServletRegistration> getServletMappedResource(String uri)
    {
    	return servletUrlMapper.getMappingObjectByUri(uri);
    }
}
