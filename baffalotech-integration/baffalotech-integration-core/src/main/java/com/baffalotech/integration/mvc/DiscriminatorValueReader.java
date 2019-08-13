package com.baffalotech.integration.mvc;

public interface DiscriminatorValueReader {

	public String parseDiscriminatorValue(IContext context,String path);
}
