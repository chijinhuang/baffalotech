package com.baffalotech.integration.http.netty.springboot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.baffalotech.integration.http.netty.springboot.server.NettyServerContainerAutoConfiguration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import({NettyServerContainerAutoConfiguration.class})
public @interface EnableNettyServerContainer {

}
