package com.picoto.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;


public abstract class HttpBaseClient {

	private static PoolingHttpClientConnectionManager cm;

	protected static final Log LOG = LogFactory.getLog(HttpBaseClient.class);

	protected static int maxConnections = ClientConfig.getIntProperty("http.client.max.connections");

	protected String path;

	private boolean authenticated;

	private ResponseData responseData;

	public HttpBaseClient() {
		responseData = new ResponseData();
	}

	public HttpBaseClient(String path) {
		this();
		this.path = path;
	}

	public static final synchronized void reset() {
		LOG.warn("Reseteando conexiones con Alfresco");
		stop();
		init();
	}

	private static final void stop() {
		try {
			cm.shutdown();
		} finally {
			cm = null;
		}
	}

	private static final void init() {
		if (cm == null) {
			synchronized (HttpBaseClient.class) {
				// Doble check, aunque es region critica hay que volver a
				// chequear cm por si acaba de ser inicializado por otro hilo
				if (cm == null) {
					cm = new PoolingHttpClientConnectionManager();
					cm.setMaxTotal(maxConnections);
					cm.setDefaultMaxPerRoute(maxConnections);
				}
			}
		}
		LOG.info("Pool -> Max: " + cm.getTotalStats().getMax() + " Disponibles: " + cm.getTotalStats().getAvailable()
				+ " Ocupadas: " + cm.getTotalStats().getLeased());
	}

	protected CloseableHttpClient getClient() {
		init();
		if (authenticated) {
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(ClientConfig.getProperty("http.client.username"),
					ClientConfig.getProperty("http.client.password"));
			provider.setCredentials(AuthScope.ANY, credentials);
			return HttpClients.custom().setDefaultCredentialsProvider(provider).setConnectionManager(cm)
					.setConnectionManagerShared(true).setKeepAliveStrategy(new CustomKeepAliveStrategy()).build();
		} else {
			return HttpClients.custom().setConnectionManager(cm).setConnectionManagerShared(true).setKeepAliveStrategy(new CustomKeepAliveStrategy()).build();
		}
	}

	protected void closeClient(CloseableHttpClient httpClient) {
		try {
			httpClient.close();
		} catch (Exception e) {
			LOG.warn("Error cerrando conexion HTTP", e);
		}
	}

	protected void closeResponse(CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (Exception e) {
				LOG.warn("Error cerrando respuesta HTTP", e);
			}
		}
	}

	public void process() {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		try {

			httpClient = getClient();
			process(httpClient);

		} catch (Exception e) {
			String msg = "Error en peticion http";
			throw new ClientException(msg, e);
		} finally {
			closeResponse(response);
			closeClient(httpClient);
		}
	}

	protected abstract void process(HttpClient httpClient) throws Exception;

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	protected final void configureTimeout(HttpRequestBase request) {
		int timeout = 1000 * ClientConfig.getIntProperty("http.client.timeout.seconds");

		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		request.setConfig(requestConfig);
	}

	public String getURL() {
		return path;
	}

	public Header[] getHeaders() {
		return responseData.getHeaders();
	}

	public void setHeaders(Header[] headers) {
		responseData.setHeaders(headers);
	}

	protected void processResponse(InputStream is) {
		try {
			responseData.setResponse(IOUtils.toByteArray(is));
		} catch (IOException e) {
			throw new ClientException("Error al leer la respuesta de la llamada HTTP", e);
		}
	}

	public byte[] getOutput() {
		return responseData.getResponse();
	}

	public String getResponse() {
		try {
			byte[] rawResponse = responseData.getResponse();
			if (rawResponse != null) {
				return IOUtils.toString(rawResponse, ClientConfig.getProperty("http.client.encoding"));
			} else {
				return "";
			}
		} catch (IOException e) {
			throw new ClientException("Error al recuperar la respuesta de la llamada HTTP", e);
		}
	}

	protected void processError(int error) {
		String errorMsg = "Error en peticion HTTP, codigo error: " + error;
		LOG.warn(errorMsg);
		throw new ClientException(errorMsg);
	}

	protected void processResponse(HttpResponse response) throws IllegalStateException, IOException {
		HttpEntity entity = response.getEntity();

		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				setHeaders(response.getAllHeaders());
				setContentType(entity.getContentType().getValue());
				processResponse(entity.getContent());
			} else {
				processError(response.getStatusLine().getStatusCode());
			}
		} finally {
			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (Exception e) {
					LOG.warn("Error consumiendo datos de conexion HTTP", e);
				}
			}
		}
	}

	protected void setContentType(String contentType) {
		responseData.setContentType(contentType);
	}

	public String getContentType() {
		return responseData.getContentType();
	}

	public ResponseData getResponseData() {
		return responseData;
	}

}
