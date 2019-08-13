package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write local server name - %v
 */
public class LocalServerNameElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		buf.append(vistor.getServerName());
	}
}