# BaffaloTech Integration
[![License](https://img.shields.io/badge/license-Apache%202-blue)](https://github.com/chijinhuang/baffalotech/blob/master/LICENSE) [![Build Status](https://travis-ci.org/chijinhuang/baffalotech.svg?branch=master)](https://travis-ci.org/chijinhuang/baffalotech) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.baffalotech/baffalotech-integration-http/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.baffalotech/baffalotech-integration-http)  [![Dependencies](https://img.shields.io/badge/Spring%20Boot-2.1.6.RELEASE-blue.svg)](https://spring.io/projects/spring-boot) [![Dependencies](https://img.shields.io/badge/Netty-4.1.37.Final-green.svg)](https://netty.io/)

## 简介
随着微服务的兴起，越来越多的项目采用微服务架构，而其中很大一部分是采用spring cloud全家桶实现。另外一方面，这些项目不同程度的是需要第三方系统的接入。这些对接方式可能80%以上都是基于http、webservice、tcp协议；安全认证方式也不尽相同。BaffaoTech Integration是基于Netty开发的、可以作为Spring Boot内嵌服务器的NIO高性能Servlet容器，可以和SpringCloud无缝集成。

## 特点
1. *支持http，webservice，tcp协议*
2. *基于Spring Boot开发，原生支持Spring Boot所有的功能*
3. *容易集成Spring Cloud的组件，例如Eureka、配置中心、zipkin、ribbon等等*
4. *http接口符合servlet规范，较低的学习成本*
5. *可以在一个jvm里面开放多个http，tcp端口，各个http端口可以使用不同的filter，servlet。*
6. *在同一个jvm里面可以发布不同的webservice*
7. *一个jvm默认开放一个http端口，和普通的spring boot内嵌容器一致*
8. *无处不在mvc框架，充分解耦业务逻辑和通用功能，让开发更加关注业务功能的开发*
9. *多种connector组件，有基于http、tcp的*
10. *所有的connector都很容易监控，按照tomcat的access log标准生成access log，方便监控接口性能*
11. *基于Netty NIO框架，所有的conector共享线程池，方便系统控制*

## 平台架构

![平台架构](https://raw.githubusercontent.com/chijinhuang/baffalotech/master/platform.png)

## 与Mule比较
| 功能  | BaffaloTech Integration| Mule|
| ---------- | -----------| -----------|
| 协议支持   | http、webservice、TCP，其他的需要定制开发  | 基本上有现成Connector的可以选择  |
| 社区支持   | 暂时只有作者一人支持  | 商业公司运作  |
| IO类型   | NIO  | BIO  |
| 底层技术栈   | 最新的Spring Boot  | 比较老的Spring版本  |
| Servlet支持   | 服务Servlet规范  | 使用http component解析http协议  |
| 集成SpringCloud   | 原生支持  | 需要改代码  |
| 学习成本   | 基于Spring Boot,学习成本低  | 学习成本相对较高  |
| 部署方式  | 跟Spring Boot一致  | 需要Mule服务器  |

## 使用

添加依赖
```
<dependency>
  <groupId>com.baffalotech</groupId>
  <artifactId>baffalotech-integration-http</artifactId>
  <version>1.0.5</version>
</dependency>
```

启用集成容器，在启动类中添加EnableNettyServerContainer，会默认创建一个http connector
```
@EnableNettyServerContainer
```

默认的http端口使用方式跟普通的Spring Boot完全一样。

新增http端口：添加NettyServerConnector的Bean就可以了
```
@Autowired
private NettyHttpServerConnectorFactory nettyHttpServerConnectorFactory;
.....
@Bean
public Connector createNettyHttpConnector()
{
	return nettyHttpServerConnectorFactory.createNettyHttpServerConnector("test",8081);
}
```

给NettyServerConector添加Filter或者Servlet，需要给servlet或者Filter指定ConnectorName
```
@Bean()
public ServletRegistrationBean cxfServlet() {
	ServletRegistrationBean<Servlet> test = new ServletRegistrationBean(NettyProxyServlet.createNettyProxyServlet("test", new CXFServlet()), "/soap-api/*");
	test.addInitParameter("bus", "testws");
	test.setOrder(1121);
	return test;
}


@Bean
public FilterRegistrationBean tracerFilter(TracingFilter httpTraceFilter) {
	FilterRegistrationBean<Filter> tracerFilterRegistrationBean = new FilterRegistrationBean<Filter>();
	tracerFilterRegistrationBean.setFilter(NettyProxyFilter.createNettyProxyFilter("test", httpTraceFilter));
	tracerFilterRegistrationBean.addUrlPatterns("/*");
	tracerFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+200);
	return tracerFilterRegistrationBean;
}
```
创建TCP Connector
```
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
```

给TCPConnector添加Connector,例如一下代码给tcp添加Zipkin支持,具体需要参考Demo中的zipkin包
```
 @Bean
    public TCPHandler tcpTracingHandler(HttpTracing httpTracing)
    {
    	TCPHandler tcpHandler = TCPTracingHandler.create(httpTracing);
    	tcpHandler.setName("hbnx");
    	return tcpHandler;
    }
```

## 鸣谢
Netty对servlet的支持是来至于github的wangzihaogithub的工程：https://github.com/wangzihaogithub/netty-servlet

