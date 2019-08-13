/*
 * Copyright 2013-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.baffalotech.integration.demo.zipkin;

import brave.Span;
import brave.http.HttpServerAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;

/** This can also parse the remote IP of the client. */
// public for others like sparkjava to use
public class TCPAdapter extends HttpServerAdapter<TCPRequest, TCPResponse> {

  /**
   * Looks for the {@link HttpServletRequest#setAttribute(String, Object) request attribute}
   * "http.route". When present, returns a response wrapper that this adapter can use to parse it.
   */
  // not static so that this can be overridden by implementations as needed.
  public TCPResponse adaptResponse(TCPRequest req, TCPResponse resp) {
    String httpRoute = (String) req.getAttribute("http.route");
    return resp;
  }

  /**
   * This sets the client IP:port to the {@linkplain HttpServletRequest#getRemoteAddr() remote
   * address} if the {@link HttpServerAdapter#parseClientIpAndPort default parsing} fails.
   */
  @Override public boolean parseClientIpAndPort(TCPRequest req, Span span) {
    if (parseClientIpFromXForwardedFor(req, span)) return true;
    return span.remoteIpAndPort(req.getRemoteHost(), req.getRemotePort());
  }

  @Override public String method(TCPRequest request) {
    return "POST";
  }

  @Override public String path(TCPRequest request) {
    return request.getLocalPort()+"";
  }

  @Override public String url(TCPRequest request) {
 
    return request.getLocalPort()+"";
  }

  @Override public String requestHeader(TCPRequest request, String name) {
    return request.getHeader(name);
  }

  /**
   * When applied to {@link #adaptResponse(HttpServletRequest, HttpServletResponse)}, returns the
   * {@link HttpServletRequest#getMethod() request method}.
   */
  @Override public String methodFromResponse(TCPResponse response) {
   
    return null;
  }

  /**
   * When applied to {@link #adaptResponse(HttpServletRequest, HttpServletResponse)}, returns the
   * {@link HttpServletRequest#getAttribute(String) request attribute} "http.route".
   */
  @Override public String route(TCPResponse response) {
    
    return null;
  }

  @Override public Integer statusCode(TCPResponse response) {
    return 200;
  }
}
