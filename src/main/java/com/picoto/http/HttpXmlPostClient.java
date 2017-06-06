package com.picoto.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public abstract class HttpXmlPostClient extends HttpPostRawClient {

	
	Document document;
	
	public HttpXmlPostClient(String path, String contentType) {
		super(path, contentType);
	}

	@Override
	protected void processResponse(InputStream is) {
		super.processResponse(is);
		this.document = parseXmlDocument(getOutput());
	}


	private Document parseXmlDocument(byte[] data)  {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(data));
			return document;
		} catch (Exception e) {
			throw new ClientException("Error parseando XML de la peticion HTTP",e);
		}
	}
	
	public Document getDocument() {
		return document;
	}

}
