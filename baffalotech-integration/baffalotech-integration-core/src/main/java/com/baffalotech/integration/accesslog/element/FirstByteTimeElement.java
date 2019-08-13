package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write time until first byte is written (commit time) in millis - %F
 */
public class FirstByteTimeElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		long commitTime = vistor.getCommitTime();
		if (commitTime == -1) {
			buf.append('-');
		} else {
			long delta = commitTime - vistor.getStartTime();
			buf.append(Long.toString(delta));
		}
	}
}