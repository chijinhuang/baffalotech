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

## 与Mule比较
--------
| 表头1  | 表头2|
| ---------- | -----------|
| 表格单元   | 表格单元   |
| 表格单元   | 表格单元   |
