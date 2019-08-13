package org.baffalotech.integration.demo;

import java.util.Date;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.xml.ws.Endpoint;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.aspectj.apache.bcel.generic.TABLESWITCH;
import org.baffalotech.integration.demo.zipkin.TCPTracingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web.Server;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

import com.baffalotech.integration.api.Connector;
import com.baffalotech.integration.http.netty.connector.NettyHttpServerConnectorFactory;
import com.baffalotech.integration.http.netty.cxf.DeptService;
import com.baffalotech.integration.http.netty.cxf.DeptServiceImpl;
import com.baffalotech.integration.http.netty.cxf.UserService;
import com.baffalotech.integration.http.netty.cxf.UserServiceImpl;
import com.baffalotech.integration.http.netty.servlet.NettyProxyFilter;
import com.baffalotech.integration.http.netty.servlet.NettyProxyServlet;
import com.baffalotech.integration.http.netty.springboot.EnableNettyServerContainer;
import com.baffalotech.integration.tcp.FixedLengthField;
import com.baffalotech.integration.tcp.LengthField;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPRequestHanlder;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.connector.NettyTCPFixedLengthServerConnectorFactory;
import com.baffalotech.integration.tcp.handler.TCPHandler;

import brave.http.HttpTracing;
import brave.servlet.TracingFilter;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@EnableNettyServerContainer
@EnableEurekaClient
public class DemoApp 
{
	@Autowired
	private NettyTCPFixedLengthServerConnectorFactory nettyTCPFixedLengthServerConnectorFactory;
	
	@Autowired
	private NettyHttpServerConnectorFactory nettyHttpServerConnectorFactory;
	
	@Autowired
	MetricsProperties properties;
	
    public static void main( String[] args )
    {
        SpringApplication.run(DemoApp.class, args);
    }
    
    @Bean
    public Connector createTCPConnector()
    {
    	TCPProtocal inTcpProtocal = new TCPProtocal();
    	inTcpProtocal.addField(new FixedLengthField("name",null, 4));
    	inTcpProtocal.addField(new LengthField("length",0, 4));
    	return nettyTCPFixedLengthServerConnectorFactory.create("hbnx", 8082, inTcpProtocal, inTcpProtocal, new TCPRequestHanlder() {
			
			@Override
			public void handle(TCPRequest tcpRequest, TCPResponse tcpResponse) {
				// TODO Auto-generated method stub
				tcpResponse.setData((new Date()).toString().getBytes());
			}
		});
    }
    
    @Bean
    public Connector createNettyHttpConnector()
    {
    	return nettyHttpServerConnectorFactory.createNettyHttpServerConnector("test",8081);
    }
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
    	return new RestTemplate();
    }
    
	@Bean()
	public ServletRegistrationBean cxfServlet() {
		ServletRegistrationBean<Servlet> test = new ServletRegistrationBean(NettyProxyServlet.createNettyProxyServlet("test", new CXFServlet()), "/soap-api/*");
		test.addInitParameter("bus", "testws");
		test.setOrder(1121);
		return test;
	}
	
	@Bean()
	public ServletRegistrationBean cxfServlet1() {
		ServletRegistrationBean<Servlet> test = new ServletRegistrationBean(new CXFServlet(), "/soap-api/*");
		test.addInitParameter("bus", "testws1");
		test.setOrder(1121);
		return test;
	}
	
	@Bean
	public FilterRegistrationBean<Filter> webServerMvcMetricsFilter(MeterRegistry registry,
			WebMvcTagsProvider tagsProvider) {
		Server serverProperties = this.properties.getWeb().getServer();
		WebMvcMetricsFilter filter = new WebMvcMetricsFilter(registry, tagsProvider,
				serverProperties.getRequestsMetricName(), serverProperties.isAutoTimeRequests());
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(NettyProxyFilter.createNettyProxyFilter("test", filter));
		registration.addUrlPatterns("/*");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
		registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean tracerFilter(TracingFilter httpTraceFilter) {
		FilterRegistrationBean<Filter> tracerFilterRegistrationBean = new FilterRegistrationBean<Filter>();
		tracerFilterRegistrationBean.setFilter(NettyProxyFilter.createNettyProxyFilter("test", httpTraceFilter));
		tracerFilterRegistrationBean.addUrlPatterns("/*");
		tracerFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+200);
		return tracerFilterRegistrationBean;
	}
	

    
    
    @Bean(name = "testws")
	public SpringBus springBus() {
		return new SpringBus();
	}
    
    @Bean(name = "testws1")
	public SpringBus springBus1() {
		return new SpringBus();
	}
	
	@Bean
    public UserService userService()
    {
        return  new UserServiceImpl();
    }
	
	@Bean
	public DeptService deptService()
	{
		return new DeptServiceImpl();
	}

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint=new EndpointImpl(springBus(), userService());//绑定要发布的服务
        endpoint.publish("/user"); //显示要发布的名称
        return endpoint;
    }
    
    @Bean
    public Endpoint endpoint1() {
        EndpointImpl endpoint=new EndpointImpl(springBus1(),deptService());//绑定要发布的服务
        endpoint.publish("/dept"); //显示要发布的名称
        return endpoint;
    }
    
    @Bean
    public TCPHandler tcpTracingHandler(HttpTracing httpTracing)
    {
    	TCPHandler tcpHandler = TCPTracingHandler.create(httpTracing);
    	tcpHandler.setName("hbnx");
    	return tcpHandler;
    }
}
