package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import javax.xml.ws.Response;

import org.omg.CORBA.Request;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write Query string (prepended with a '?' if it exists) - %q
 */
public class QueryElement implements AccessLogElement {

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		// TODO Auto-generated method stub
		String query = vistor.getQueryString();

		if (query != null) {
			buf.append('?');
			buf.append(query);
		}
	}
}