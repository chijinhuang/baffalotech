package com.baffalotech.integration.accesslog;

import java.io.CharArrayWriter;
import java.util.Date;

/**
 * 参考tomcat access log pattern的方式，现扩充功能，需要支持tcp或其他协议,其中vistor是新加的
 * @author chijinhuang
 *
 */
public interface AccessLogElement {
	public void addElement(CharArrayWriter buf, Date date, long time,AccessLogElementVistor vistor);
}