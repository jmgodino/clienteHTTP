package com.picoto.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

public abstract class HttpGetClient extends HttpBaseClient {

	
	public HttpGetClient(String path) {
		super(path);
	}

	@Override
	protected void process(HttpClient httpClient) throws Exception {
		List<NameValuePair> emptyParams = new ArrayList<NameValuePair>();
		List<NameValuePair> realParams = processRequest(emptyParams);
		String queryParams = "";
		if (realParams != null && realParams.size() > 0) {
			queryParams = new URIBuilder().addParameters(realParams).build().toASCIIString();
		}
		HttpGet httpGet = new HttpGet(getURL()+queryParams);
		configureTimeout(httpGet);
		LOG.debug("Haciendo GET a la URL: "+getURL()+" "+httpGet.toString());
		
		HttpResponse response = httpClient.execute(httpGet);
		
		processResponse(response);
	}

	protected abstract List<NameValuePair> processRequest(List<NameValuePair> params);

	
}
