package org.baffalotech.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class APIGateway 
{
    public static void main( String[] args )
    {
        SpringApplication.run(APIGateway.class, args);
    }
}
