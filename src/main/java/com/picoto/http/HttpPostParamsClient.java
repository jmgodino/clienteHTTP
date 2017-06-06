package com.picoto.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

public abstract class HttpPostParamsClient extends HttpBaseClient {

	public HttpPostParamsClient(String path) {
		super(path);
	}

	@Override
	protected void process(HttpClient httpClient) throws Exception {
		HttpPost httpPost = new HttpPost(getURL());
		configureTimeout(httpPost);
		LOG.debug("Haciendo POST a la URL: " + getURL());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		httpPost.setEntity(new UrlEncodedFormEntity(processRequest(params),
				ClientConfig.getProperty("http.client.encoding")));

		HttpResponse response = httpClient.execute(httpPost);

		processResponse(response);
	}

	protected abstract List<NameValuePair> processRequest(
			List<NameValuePair> params);

}
