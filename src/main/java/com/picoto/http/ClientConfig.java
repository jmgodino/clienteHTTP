package com.picoto.http;

import java.util.ResourceBundle;

public class ClientConfig {

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle("clientHTTP");

	public static int getIntProperty(String string) {
		try {
			return new Integer(getProperty(string));
		} catch (Exception e) {
			return 0;
		}
	}

	public static String getProperty(String string) {
		return bundle.getString(string);
	}
}
