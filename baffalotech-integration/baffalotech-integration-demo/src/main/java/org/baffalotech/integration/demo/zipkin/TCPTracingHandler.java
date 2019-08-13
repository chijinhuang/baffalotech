package org.baffalotech.integration.demo.zipkin;

import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.handler.AbstractTCPHandler;
import com.baffalotech.integration.tcp.handler.TCPHandlerChain;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.Tracing;
import brave.http.HttpServerHandler;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext;
import io.netty.channel.ChannelHandlerContext;

public class TCPTracingHandler extends AbstractTCPHandler {

	static final Getter<TCPRequest, String> GETTER = new Getter<TCPRequest, String>() {
		@Override
		public String get(TCPRequest carrier, String key) {
			return carrier.getHeader(key);
		}

		@Override
		public String toString() {
			return "TCPRequest::getAttribute";
		}
	};
	static final TCPAdapter ADAPTER = new TCPAdapter();

	public static TCPTracingHandler create(Tracing tracing) {
		return new TCPTracingHandler(HttpTracing.create(tracing));
	}

	public static TCPTracingHandler create(HttpTracing httpTracing) {
		return new TCPTracingHandler(httpTracing);
	}

	final CurrentTraceContext currentTraceContext;
	final Tracer tracer;
	final HttpServerHandler<TCPRequest, TCPResponse> handler;
	final TraceContext.Extractor<TCPRequest> extractor;

	TCPTracingHandler(HttpTracing httpTracing) {
		tracer = httpTracing.tracing().tracer();
		currentTraceContext = httpTracing.tracing().currentTraceContext();
		handler = HttpServerHandler.create(httpTracing, ADAPTER);
		extractor = httpTracing.tracing().propagation().extractor(GETTER);
	}

	@Override
	public void handle(ChannelHandlerContext ctx, TCPRequest tcpRequest, TCPResponse tcpResponse,
			TCPHandlerChain chain) {
		// TODO Auto-generated method stub
		Span span = handler.handleReceive(extractor, tcpRequest);

		// Add attributes for explicit access to customization or span context
		tcpRequest.setAttribute(SpanCustomizer.class.getName(), span.customizer());
		tcpRequest.setAttribute(TraceContext.class.getName(), span.context());

		Throwable error = null;
		Scope scope = currentTraceContext.newScope(span.context());

		try {
			// any downstream code can see Tracer.currentSpan() or use
			// Tracer.currentSpanCustomizer()
			chain.handle(ctx, tcpRequest, tcpResponse);
		} catch (RuntimeException | Error e) {
			error = e;
			throw e;
		} finally {
			scope.close();
			handler.handleSend(ADAPTER.adaptResponse(tcpRequest, tcpResponse), error, span);
		}
	}
}
