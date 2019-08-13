package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write local or remote port for request connection - %p and %{xxx}p
 */
public class PortElement implements AccessLogElement {

	/**
	 * Type of port to log
	 */
	private static final String localPort = "local";
	private static final String remotePort = "remote";

	private final PortType portType;

	public PortElement() {
		portType = PortType.LOCAL;
	}

	public PortElement(String type) {
		switch (type) {
		case remotePort:
			portType = PortType.REMOTE;
			break;
		case localPort:
			portType = PortType.LOCAL;
			break;
		default:
			portType = PortType.LOCAL;
			break;
		}
	}

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		if (portType == PortType.LOCAL) {
			buf.append(Integer.toString(vistor.getServerPort()));
		} else {
			buf.append(Integer.toString(vistor.getRemotePort()));
		}
	}
}