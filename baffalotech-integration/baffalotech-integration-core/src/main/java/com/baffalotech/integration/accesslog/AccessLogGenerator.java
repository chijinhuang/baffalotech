package com.baffalotech.integration.accesslog;

import java.io.CharArrayWriter;
import java.util.Date;

public class AccessLogGenerator {
	
	private final AccessLogElement[] accessLogElements;
	
	public AccessLogGenerator(String pattern)
	{
		this.accessLogElements = AccessLogElementParser.parsePattern(pattern);
	}
	
	public String generateLog(Date date,long time,AccessLogElementVistor vistor)
	{
		CharArrayWriter buf = new CharArrayWriter();
		for(AccessLogElement element : accessLogElements)
		{
			element.addElement(buf, date, time, vistor);
		}
		return buf.toString();
	}
}
