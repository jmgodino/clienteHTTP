package com.picoto.http;

import java.io.ByteArrayInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;

public abstract class HttpPostRawClient extends HttpBaseClient {

	private String contentType;
	
	public HttpPostRawClient(String path, String contentType) {
		super(path);
		this.contentType = contentType;
	}


	@Override
	protected void process(HttpClient httpClient) throws Exception {
		HttpPost httpPost = new HttpPost(getURL());
		configureTimeout(httpPost);
		LOG.debug("Haciendo POST a la URL: " + getURL());

		InputStreamEntity reqEntity = new InputStreamEntity(
				new ByteArrayInputStream(processRequest().getBytes()));
		reqEntity.setContentType(contentType);
		reqEntity.setContentEncoding(ClientConfig
				.getProperty("http.client.encoding"));

		httpPost.setEntity(reqEntity);

		HttpResponse response = httpClient.execute(httpPost);

		processResponse(response);
	}

	protected abstract String processRequest();

}
