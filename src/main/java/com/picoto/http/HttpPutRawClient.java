package com.picoto.http;

import java.io.ByteArrayInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;

public abstract class HttpPutRawClient extends HttpBaseClient {

	private String contentType;

	public HttpPutRawClient(String path, String contentType) {
		super(path);
		this.contentType = contentType;
	}

	@Override
	protected void process(HttpClient httpClient) throws Exception {
		HttpPut httpPut = new HttpPut(getURL());
		configureTimeout(httpPut);
		LOG.debug("Haciendo PUT a la URL: " + getURL());

		InputStreamEntity reqEntity = new InputStreamEntity(
				new ByteArrayInputStream(processRequest().getBytes(
						ClientConfig.getProperty("http.client.encoding"))));

		reqEntity.setContentType(contentType);
		reqEntity.setContentEncoding(ClientConfig
				.getProperty("http.client.encoding"));

		httpPut.setEntity(reqEntity);

		HttpResponse response = httpClient.execute(httpPut);

		processResponse(response);
	}

	protected abstract String processRequest();

}
