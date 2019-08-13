package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;
import java.util.Iterator;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
     * write a specific response header - %{xxx}o
     */
    public class ResponseHeaderElement implements AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, long time,AccessLogElementVistor vistor) {
            if (null != vistor) {
                Iterator<String> iter = vistor.getResponseHeaders(header).iterator();
                if (iter.hasNext()) {
                    buf.append(iter.next());
                    while (iter.hasNext()) {
                        buf.append(',').append(iter.next());
                    }
                    return;
                }
            }
            buf.append('-');
        }
    }