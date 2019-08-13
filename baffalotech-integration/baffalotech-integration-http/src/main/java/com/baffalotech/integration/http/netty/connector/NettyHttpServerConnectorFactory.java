package com.baffalotech.integration.http.netty.connector;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.servlet.ServletRegistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.core.ServerConstanst;
import com.baffalotech.integration.http.netty.servlet.NettyDefaultHttpServlet;
import com.baffalotech.integration.http.netty.servlet.NettyServletContext;
import com.baffalotech.integration.http.netty.springboot.server.HttpServletProtocolsRegisterSpringAdapter;

public class NettyHttpServerConnectorFactory implements ResourceLoaderAware{
	
	@Autowired
	private AbstractServletWebServerFactory servletWebServerFactory;
	
	private ResourceLoader resourceLoader;
	
	@Autowired
	private IntegrationServerProperties integrationProperties;
	
	@Autowired
	private Container container;
	
	public NettyHttpServerConnector createNettyHttpServerConnector(String name,int port)
	{
		try {
            //临时目录
            File docBase = createTempDir("http-server-"+name,port);

            //服务器端口
            InetSocketAddress serverAddress =  new InetSocketAddress(servletWebServerFactory.getAddress() == null? InetAddress.getLoopbackAddress():servletWebServerFactory.getAddress(),port);
            ClassLoader classLoader = resourceLoader != null ? resourceLoader.getClassLoader() : ClassUtils.getDefaultClassLoader();
            
            NettyHttpServerConnector serverConnector = new NettyHttpServerConnector(container);
            serverConnector.setPort(port);
            NettyServletContext servletContext = new NettyServletContext(serverAddress,classLoader,docBase.getAbsolutePath());

            serverConnector.addProtocolsRegister(new HttpServletProtocolsRegisterSpringAdapter(integrationProperties,servletContext,container.getServerExecutor(),servletWebServerFactory));

            //默认 servlet
            if (servletWebServerFactory.isRegisterDefaultServlet()) {
                NettyDefaultHttpServlet defaultServlet = new NettyDefaultHttpServlet();
                ServletRegistration servletRegistration = servletContext.addServlet("default",defaultServlet);
                if(servletRegistration != null)
                {
                	servletRegistration.addMapping("/");
                }
            }
            serverConnector.setName(name);
            serverConnector.setSchema(ServerConstanst.HTTP_SCHEMA_TYPE);
            servletContext.setServletContextName(serverConnector.getName());
            serverConnector.setServletContext(servletContext);
            return serverConnector;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
		
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// TODO Auto-generated method stub
		this.resourceLoader = resourceLoader;
	}
	
	protected final File createTempDir(String prefix,int port) {
		try {
			File tempDir = File.createTempFile(prefix + ".", "." + port);
			tempDir.delete();
			tempDir.mkdir();
			tempDir.deleteOnExit();
			return tempDir;
		}
		catch (IOException ex) {
			throw new WebServerException(
					"Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
		}
	}

}
