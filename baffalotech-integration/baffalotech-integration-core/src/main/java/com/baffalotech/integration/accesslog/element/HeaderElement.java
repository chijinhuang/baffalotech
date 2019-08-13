package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;
import java.util.Enumeration;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
     * write incoming headers - %{xxx}i
     */
   public class HeaderElement implements AccessLogElement {
        private final String header;

        public HeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date,long time,AccessLogElementVistor vistor) {
            Enumeration<String> iter = vistor.getHeaders(header);
            if (iter.hasMoreElements()) {
                buf.append(iter.nextElement());
                while (iter.hasMoreElements()) {
                    buf.append(',').append(iter.nextElement());
                }
                return;
            }
            buf.append('-');
        }
   }