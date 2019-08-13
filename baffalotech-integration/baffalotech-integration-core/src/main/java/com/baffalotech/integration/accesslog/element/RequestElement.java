package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
     * write first line of the request (method and request URI) - %r
     */
    public class RequestElement implements AccessLogElement {
        @Override
        public void addElement(CharArrayWriter buf, Date date,  long time,AccessLogElementVistor vistor) {
            if (vistor != null) {
                String method = vistor.getMethod();
                if (method == null) {
                    // No method means no request line
                    buf.append('-');
                } else {
                    buf.append(vistor.getMethod());
                    buf.append(' ');
                    buf.append(vistor.getRequestURI());
                    if (vistor.getQueryString() != null) {
                        buf.append('?');
                        buf.append(vistor.getQueryString());
                    }
                    buf.append(' ');
                    buf.append(vistor.getProtocol());
                }
            } else {
                buf.append('-');
            }
        }
    }