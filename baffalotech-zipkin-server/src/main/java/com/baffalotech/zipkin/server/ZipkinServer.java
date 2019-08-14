package com.baffalotech.zipkin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zipkin2.server.internal.EnableZipkinServer;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableZipkinServer
public class ZipkinServer 
{
    public static void main( String[] args )
    {
        SpringApplication.run(ZipkinServer.class, args);
    }
}
