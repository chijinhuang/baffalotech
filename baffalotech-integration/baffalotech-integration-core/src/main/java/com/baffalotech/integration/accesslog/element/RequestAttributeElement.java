package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
     * write an attribute in the ServletRequest - %{xxx}r
     */
    public class RequestAttributeElement implements AccessLogElement {
        private final String header;

        public RequestAttributeElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date,  long time,AccessLogElementVistor vistor) {
            Object value = null;
            if (vistor != null) {
                value = vistor.getAttribute(header);
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    buf.append((String) value);
                } else {
                    buf.append(value.toString());
                }
            } else {
                buf.append('-');
            }
        }
    }