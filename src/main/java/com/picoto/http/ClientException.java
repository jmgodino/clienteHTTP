package com.picoto.http;

public class ClientException extends RuntimeException {


	private static final long serialVersionUID = -793693677411694031L;

	public ClientException() {
		super();
	}

	public ClientException(String message) {
		super(message);
	}

	public ClientException(Throwable cause) {
		super(cause);
	}

	public ClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
