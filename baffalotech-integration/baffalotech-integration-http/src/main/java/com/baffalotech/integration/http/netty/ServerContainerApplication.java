package com.baffalotech.integration.http.netty;

import javax.servlet.Servlet;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.baffalotech.integration.http.netty.cxf.DeptService;
import com.baffalotech.integration.http.netty.cxf.DeptServiceImpl;
import com.baffalotech.integration.http.netty.cxf.UserService;
import com.baffalotech.integration.http.netty.cxf.UserServiceImpl;
import com.baffalotech.integration.http.netty.servlet.NettyProxyServlet;
import com.baffalotech.integration.http.netty.springboot.EnableNettyServerContainer;
import com.baffalotech.integration.tcp.FixedLengthField;
import com.baffalotech.integration.tcp.LengthField;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPRequestHanlder;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.connector.NettyTCPFixedLengthServerConnector;
import com.baffalotech.integration.tcp.connector.NettyTCPFixedLengthServerConnectorFactory;

@SpringBootApplication
@EnableNettyServerContainer
public class ServerContainerApplication {
	
	@Autowired
	private NettyTCPFixedLengthServerConnectorFactory nettyTCPFixedLengthServerConnectorFactory;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ServerContainerApplication.class, args);
	}
	
	@Bean
	public ServletRegistrationBean cxfServlet() {
		ServletRegistrationBean<Servlet> test = new ServletRegistrationBean(NettyProxyServlet.createNettyProxyServlet("test", new CXFServlet()), "/soap-api/*");
		test.setOrder(1121);
		return test;
	}
	
	@Bean
	public ServletRegistrationBean cxfServlet1() {
		ServletRegistrationBean<Servlet> test = new ServletRegistrationBean(new CXFServlet(), "/soap-api/*");
		test.setOrder(1121);
		return test;
	}

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
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
        EndpointImpl endpoint=new EndpointImpl(springBus(),deptService());//绑定要发布的服务
        endpoint.publish("/dept"); //显示要发布的名称
        return endpoint;
    }
    
    @Bean
    public NettyTCPFixedLengthServerConnector nettyTCPFixedLengthServerConnector()
    {
    	TCPProtocal inTcpProtocal = new TCPProtocal();
    	inTcpProtocal.addField(new FixedLengthField("name", null, 4));
    	inTcpProtocal.addField(new FixedLengthField("inteface", null, 4));
    	inTcpProtocal.addField(new LengthField("length", 0, 4));
    	return nettyTCPFixedLengthServerConnectorFactory.create("tcp", 8085, inTcpProtocal, inTcpProtocal, new TCPRequestHanlder() {
			
			@Override
			public void handle(TCPRequest tcpRequest, TCPResponse tcpResponse) {
				// TODO Auto-generated method stub
				tcpResponse.setData("hello world".getBytes());
			}
		});
    }

}
