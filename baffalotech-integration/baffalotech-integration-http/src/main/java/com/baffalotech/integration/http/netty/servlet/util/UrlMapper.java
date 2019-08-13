package com.baffalotech.integration.http.netty.servlet.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.baffalotech.integration.http.netty.servlet.pathmap.MappedResource;
import com.baffalotech.integration.http.netty.servlet.pathmap.PathMappings;

/**
 * url映射
 *
 * 映射规范
 * 在web应用部署描述符中，以下语法用于定义映射：
 * ■  以‘/’字符开始、以‘/*’后缀结尾的字符串用于路径匹配。
 * ■  以‘*.’开始的字符串用于扩展名映射。
 * ■  空字符串“”是一个特殊的URL模式，其精确映射到应用的上下文根，即，http://host:port/context-root/
 * 请求形式。在这种情况下，路径信息是‘/’且servlet路径和上下文路径是空字符串（“”）。
 * ■  只包含“/”字符的字符串表示应用的“default”servlet。在这种情况下，servlet路径是请求URL减去上
 * 下文路径且路径信息是null。
 * ■  所以其他字符串仅用于精确匹配。
 * 如果一个有效的web.xml（在从fragment 和注解合并了信息后）包含人任意的url-pattern，其映射到多个servlet，那么部署将失败。
 *
 *
 * 示例映射集合
 * 请看下面的一组映射：
 *  表12-1  示例映射集合
 *      Path Pattern            Servlet
 *
 *      /foo/bar/*              servlet1
 *      /baz/*                  servlet2
 *      /catalog                servlet3
 *      *.bop                   servlet4
 * 将产生以下行为：
 *  表12-2   传入路径应用于示例映射
 *      Incoming Path           Servlet Handling Request
 *
 *      /foo/bar/index.html     servlet1
 *      /foo/bar/index.bop      servlet1
 *      /baz                    servlet2
 *      /baz/index.html         servlet2
 *      /catalog                servlet3
 *      /catalog/index.html    “default”  servlet
 *      /catalog/racecar.bop    servlet4
 *      /index.bop              servlet4
 * 请注意，在/catalog/index.html和/catalog/racecar.bop的情况下，不使用映射到“/catalog”的servlet，因为不是精确匹配的
 * 
 * 改造该代码，代码从jetty servlet path mapping规则
 *
 * @author acer01
 * Created on 2017-08-25 11:32.
 */
public class UrlMapper<T> {
	
    private PathMappings<T> pathMappings = new PathMappings<T>();
    
    private List<PathMappings<T>> filterPathMappings = new ArrayList<PathMappings<T>>();
    
    private boolean singleFlag = false;
    
    public UrlMapper(boolean singleFlag)
    {
    	this.singleFlag = singleFlag;
    }

    /**
     * 增加映射关系
     *
     * @param urlPattern  urlPattern
     * @param object     对象
     * @param objectName 对象名称
     * @throws IllegalArgumentException 异常
     */
    public void addMapping(String urlPattern, T object, String objectName) throws IllegalArgumentException {
        Objects.requireNonNull(urlPattern);
        Objects.requireNonNull(object);
        Objects.requireNonNull(objectName);
        if(singleFlag)
        {
        	 pathMappings.removeIf(mappedResource -> mappedResource.getPathSpec().equals(PathMappings.asPathSpec(urlPattern)));
        	 pathMappings.put(urlPattern, object);
        }else {
        	PathMappings<T> mappings = new PathMappings<T>();
        	mappings.put(urlPattern, object);
        	filterPathMappings.add(mappings);
        }
    }

    /**
     * 获取一个映射对象
     * @param absoluteUri 绝对路径
     * @return
     */
    public MappedResource<T> getMappingObjectByUri(String absoluteUri) {
        
        return pathMappings.getMatch(absoluteUri);
    }

    /**
     * 获取多个映射对象
     * @param absoluteUri 绝对路径
     * @return
     */
    public List<T> getMappingObjectsByUri(String absoluteUri,List<T> list) {
       filterPathMappings.forEach(mappings ->{
    	   MappedResource<T> resource = mappings.getMatch(absoluteUri);
    	   if(resource != null)
    	   {
    		   list.add(resource.getResource());
    	   }
       });
        return list;
    }
}