package com.picoto.http;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

public class ResponseData {

	private Header[] headers;

	private byte[] response;

	private String contentType;

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public byte[] getResponse() {
		return response;
	}

	public void setResponse(byte[] response) {
		this.response = response;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getHeader(String name) {
		for (Header header : headers) {
			if (StringUtils.endsWithIgnoreCase(header.getName(), name)) {
				return header.getValue();
			}
		}
		return "";
	}

}
