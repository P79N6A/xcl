package van.util.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HttpClientService
 * @author Yang Yun Long
 *
 */
public class HttpClientService {

	private static final String DEFAULT_USER_AGENT = 
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36";
	
	private RequestConfig requestConfig;
	private CloseableHttpClient httpClient;
	
	class CloseExpiredConnectionTask extends Thread {
		private PoolingHttpClientConnectionManager connMgr;
		public CloseExpiredConnectionTask(PoolingHttpClientConnectionManager connMgr) {
			this.setDaemon(true);
			this.connMgr = connMgr;
		}
		public void run() {
			try {
				for (;;) {
					synchronized (this) {
						wait(30000L);
						connMgr.closeExpiredConnections();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public HttpClientService() {
		SSLContext sslcontext = createIgnoreVerifySSL();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
				.register("http", PlainConnectionSocketFactory.INSTANCE)  
				.register("https", new SSLConnectionSocketFactory(sslcontext))  
				.build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connManager.setMaxTotal(2);
		connManager.setDefaultMaxPerRoute(1);
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setConnectionManager(connManager);
		httpClient = builder.build();
		Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectTimeout(30*60*1000);
		configBuilder.setConnectionRequestTimeout(30*60*1000); // 15mins
		configBuilder.setSocketTimeout(30*60*1000);
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
		CloseExpiredConnectionTask task = new CloseExpiredConnectionTask(connManager);
		task.start();
	}
	
	public static SSLContext createIgnoreVerifySSL() {
		try {
			SSLContext sc = SSLContext.getInstance("SSLv3");
			// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
			X509TrustManager trustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sc.init(null, new TrustManager[] { trustManager }, null);
			return sc;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	private CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * Handle GET request
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String doGet(String url) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(this.requestConfig);
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return null;
	}
	
	/**
	 * Handle GET request with given parameters
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String doGet(String url, Map<String, String> params) throws ClientProtocolException, IOException, URISyntaxException {
		System.out.println("HttpClientService.doGet [url: " + url + ", params: " + params.toString() + "]");
		if (!params.isEmpty()) {
			URIBuilder uriBuilder = new URIBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				uriBuilder.addParameter(entry.getKey(), entry.getValue());
			}
			return this.doGet(uriBuilder.build().toString());
		} else {
			return this.doGet(url);
		}
	}

	/**
	 * Handle POST request
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPost(String url) throws ClientProtocolException, IOException {
		return doPost(url, new HashMap<String, String>());
	}
	
	/**
	 * Handle POST request with given parameters
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPost(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		return doPost(url, params, null);
	}

	/**
	 * Handle POST request with given parameters and headers
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPost(String url, Map<String, String> params, Map<String, String> headers) throws ClientProtocolException, IOException {
		System.out.println("HttpClientService.doPost [url: " + url + ", params: " + params.toString() + "]");
		HttpPost httpPost = new HttpPost(url);
		if (null != params && !params.isEmpty()) {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
			httpPost.setEntity(entity);
		}

		httpPost.setConfig(this.requestConfig);
		httpPost.setHeader("User-Agent", DEFAULT_USER_AGENT);
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				httpPost.setHeader(header.getKey(), header.getValue());
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"), response.toString());
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * Handle POST request with given json string
	 * @param url
	 * @param json
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPostJson(String url, String json) throws ClientProtocolException, IOException {
		System.out.println("HttpClientService.doPost [url: " + url + ", params: " + json + "]");
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(this.requestConfig);
		if (json != null) {
			StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(stringEntity);
		}
		CloseableHttpResponse response = null;
		try {
			response = this.getHttpClient().execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"), response.toString());
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

}
