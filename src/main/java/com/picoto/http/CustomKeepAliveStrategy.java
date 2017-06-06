package com.picoto.http;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

public class CustomKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

	@Override
	public long getKeepAliveDuration(HttpResponse resp, HttpContext ctx) {
		long serverDefinedTime = super.getKeepAliveDuration(resp, ctx);
		if (serverDefinedTime > 0) {
			return serverDefinedTime;
		} else {
			return 1000*ClientConfig.getIntProperty("http.client.keepalive.timeout.seconds");
		}
	}

}
