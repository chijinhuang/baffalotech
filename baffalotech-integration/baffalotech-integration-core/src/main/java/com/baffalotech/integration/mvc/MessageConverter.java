package com.baffalotech.integration.mvc;

public interface MessageConverter<T> {

	public T decode(String payload,Class<T> convertType);
	
	public String encode(T value);
}
