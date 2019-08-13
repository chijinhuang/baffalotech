package com.baffalotech.integration.accesslog;

import java.util.ArrayList;
import java.util.List;

import com.baffalotech.integration.accesslog.element.ByteSentElement;
import com.baffalotech.integration.accesslog.element.ConnectionStatusElement;
import com.baffalotech.integration.accesslog.element.CookieElement;
import com.baffalotech.integration.accesslog.element.DateAndTimeElement;
import com.baffalotech.integration.accesslog.element.ElapsedTimeElement;
import com.baffalotech.integration.accesslog.element.FirstByteTimeElement;
import com.baffalotech.integration.accesslog.element.HeaderElement;
import com.baffalotech.integration.accesslog.element.HostElement;
import com.baffalotech.integration.accesslog.element.HttpStatusCodeElement;
import com.baffalotech.integration.accesslog.element.LocalAddrElement;
import com.baffalotech.integration.accesslog.element.LocalServerNameElement;
import com.baffalotech.integration.accesslog.element.LogicalUserNameElement;
import com.baffalotech.integration.accesslog.element.MethodElement;
import com.baffalotech.integration.accesslog.element.PortElement;
import com.baffalotech.integration.accesslog.element.ProtocolElement;
import com.baffalotech.integration.accesslog.element.QueryElement;
import com.baffalotech.integration.accesslog.element.RemoteAddrElement;
import com.baffalotech.integration.accesslog.element.RequestAttributeElement;
import com.baffalotech.integration.accesslog.element.RequestElement;
import com.baffalotech.integration.accesslog.element.RequestURIElement;
import com.baffalotech.integration.accesslog.element.ResponseHeaderElement;
import com.baffalotech.integration.accesslog.element.SessionAttributeElement;
import com.baffalotech.integration.accesslog.element.SessionIdElement;
import com.baffalotech.integration.accesslog.element.StringElement;
import com.baffalotech.integration.accesslog.element.ThreadNameElement;
import com.baffalotech.integration.accesslog.element.UserElement;

public class AccessLogElementParser {

	private String pattern;

	private AccessLogElementParser(String pattern) {
		this.pattern = pattern;
	}
	
	public static AccessLogElement[] parsePattern(String pattern)
	{
		return new AccessLogElementParser(pattern).createLogElements();
	}

	/**
	 * Parse pattern string and create the array of AccessLogElement.
	 * 
	 * @return the log elements array
	 */
	protected AccessLogElement[] createLogElements() {
		List<AccessLogElement> list = new ArrayList<>();
		boolean replace = false;
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (replace) {
				/*
				 * For code that processes {, the behavior will be ... if I do not encounter a
				 * closing } - then I ignore the {
				 */
				if ('{' == ch) {
					StringBuilder name = new StringBuilder();
					int j = i + 1;
					for (; j < pattern.length() && '}' != pattern.charAt(j); j++) {
						name.append(pattern.charAt(j));
					}
					if (j + 1 < pattern.length()) {
						/* the +1 was to account for } which we increment now */
						j++;
						list.add(createAccessLogElement(name.toString(), pattern.charAt(j)));
						i = j; /* Since we walked more than one character */
					} else {
						// D'oh - end of string - pretend we never did this
						// and do processing the "old way"
						list.add(createAccessLogElement(ch));
					}
				} else {
					list.add(createAccessLogElement(ch));
				}
				replace = false;
			} else if (ch == '%') {
				replace = true;
				list.add(new StringElement(buf.toString()));
				buf = new StringBuilder();
			} else {
				buf.append(ch);
			}
		}
		if (buf.length() > 0) {
			list.add(new StringElement(buf.toString()));
		}
		return list.toArray(new AccessLogElement[0]);
	}

	/**
	 * Create an AccessLogElement implementation which needs an element name.
	 * 
	 * @param name    Header name
	 * @param pattern char in the log pattern
	 * @return the log element
	 */
	protected AccessLogElement createAccessLogElement(String name, char pattern) {
		switch (pattern) {
		case 'i':
			return new HeaderElement(name);
		case 'c':
			return new CookieElement(name);
		case 'o':
			return new ResponseHeaderElement(name);
		case 'p':
			return new PortElement(name);
		case 'r':
			return new RequestAttributeElement(name);
		case 's':
			return new SessionAttributeElement(name);
		case 't':
			return new DateAndTimeElement(name);
		default:
			return new StringElement("???");
		}
	}
	
	/**
     * Create an AccessLogElement implementation.
     * @param pattern char in the log pattern
     * @return the log element
     */
    protected AccessLogElement createAccessLogElement(char pattern) {
        switch (pattern) {
        case 'a':
            return new RemoteAddrElement();
        case 'A':
            return new LocalAddrElement(false);
        case 'b':
            return new ByteSentElement(true);
        case 'B':
            return new ByteSentElement(false);
        case 'D':
            return new ElapsedTimeElement(true);
        case 'F':
            return new FirstByteTimeElement();
        case 'h':
            return new HostElement();
        case 'H':
            return new ProtocolElement();
        case 'l':
            return new LogicalUserNameElement();
        case 'm':
            return new MethodElement();
        case 'p':
            return new PortElement();
        case 'q':
            return new QueryElement();
        case 'r':
            return new RequestElement();
        case 's':
            return new HttpStatusCodeElement();
        case 'S':
            return new SessionIdElement();
        case 't':
            return new DateAndTimeElement();
        case 'T':
            return new ElapsedTimeElement(false);
        case 'u':
            return new UserElement();
        case 'U':
            return new RequestURIElement();
        case 'v':
            return new LocalServerNameElement();
        case 'I':
            return new ThreadNameElement();
        case 'X':
            return new ConnectionStatusElement();
        default:
            return new StringElement("???" + pattern + "???");
        }
    }
}
