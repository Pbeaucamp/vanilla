package bpm.geoloc.creator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.geoloc.creator.model.CkanResource;

public class CkanHelper {

	public static final String GET_PACKAGE_INFO = "/api/3/action/package_show?id=";
	public static final String CREATE_RESOURCE = "/api/3/action/resource_create";
	public static final String UPDATE_RESOURCE = "/api/3/action/resource_update";
	public static final String DATASTORE_SEARCH = "/api/3/action/datastore_search";
	public static final String DATASTORE_UPDATE = "/api/3/action/datastore_create";
	
	public static final String DATAPUSHER_PUSH_DATASTORE = "/api/datapusher/";
	
	public static String getPackageName(String ckanUrl, String packageName) throws Exception {
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_PACKAGE_INFO + packageName);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get informations for this dataset " + packageName + ".");
		}
		
		JSONObject jsonObject = new JSONObject(jsonStatus);
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;
		String id = !resultJson.isNull("id") ? resultJson.getString("id") : null;

		if (id == null) {
			return null;
		}
		
		return resultJson.getString("name");
	}
	
	public static CkanResource getCkanResourceURL(String ckanUrl, String packageName, String selectedResourceId) throws Exception {
		String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, GET_PACKAGE_INFO + packageName);
		if (jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Unable to get informations for this dataset " + packageName + ".");
		}

		JSONObject jsonObject = new JSONObject(jsonStatus);
		JSONObject resultJson = !jsonObject.isNull("result") ? jsonObject.getJSONObject("result") : null;
		String id = !resultJson.isNull("id") ? resultJson.getString("id") : null;

		if (id == null) {
			return null;
		}

		JSONArray jsonResources = !resultJson.isNull("resources") ? resultJson.getJSONArray("resources") : null;
		if (jsonResources != null) {
			for (int i = 0; i < jsonResources.length(); i++) {
				try {
					JSONObject jsonResource = (JSONObject) jsonResources.get(i);
					String resourceId = !jsonResource.isNull("id") ? jsonResource.getString("id") : null;
					
					if (resourceId.equals(selectedResourceId)) {
						String resourceName = !jsonResource.isNull("name") ? jsonResource.getString("name") : null;
						String resourceFormat = !jsonResource.isNull("format") ? jsonResource.getString("format") : null;
						String resourceUrl = !jsonResource.isNull("url") ? jsonResource.getString("url") : null;
						return new CkanResource(resourceId, resourceName, resourceFormat, resourceUrl);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public static String uploadCkanFile(String ckanUrl, String apiKey, String fileName, String format, String filePath, String packId, String resourceId) throws Exception {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String date = dateFormatGmt.format(new Date());
		String action = null;
		HttpEntity entity = null;

		File initialFile = new File(filePath);
		ContentBody cbFile = new FileBody(initialFile, ContentType.TEXT_PLAIN);
		if (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) {
			if (resourceId != null && !resourceId.isEmpty()) {

				JSONArray fields = getResourceFields(ckanUrl, apiKey, resourceId);

				MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("key", 
						new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
						.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
						.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
						.addPart("package_id", new StringBody(packId, ContentType.TEXT_PLAIN))
						.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
//						.addPart("file", cbFile)
						.addPart("upload", cbFile);
				multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));
				HttpEntity reqEntity = multipart.build();

				action = UPDATE_RESOURCE;

				String result = callCkan(ckanUrl, action, apiKey, reqEntity, true);

				TimeUnit.SECONDS.sleep(2);

				if (result.split("\n")[0].equals("200") && fields != null) {

					JSONArray filtered = new JSONArray();
					for (int i = 0; i < fields.length(); i++) {
						JSONObject f = fields.getJSONObject(i);
						if (f.getString("id").equals("_id")) {
							continue;
						}
						filtered.put(f);
					}

					JSONObject json = new JSONObject();
					json.put("resource_id", resourceId);
					json.put("force", "true");
					json.put("fields", filtered);

					action = DATASTORE_UPDATE;
					entity = new StringEntity(json.toString(), "UTF-8");
				}
				else {
					return result;
				}
			}
			else {
				action = CREATE_RESOURCE;

				MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
						.addPart("key", new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
						.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
						.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
						.addPart("package_id", new StringBody(packId, ContentType.TEXT_PLAIN))
						.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
//						.addPart("file", cbFile)
						.addPart("upload", cbFile);
				if (resourceId != null && !resourceId.isEmpty()) {
					multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));

					action = UPDATE_RESOURCE;
				}

				entity = multipart.build();
			}
		}
		else {
			action = CREATE_RESOURCE;

			MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
					.addPart("key", new StringBody(fileName + "_" + date, ContentType.TEXT_PLAIN))
					.addPart("name", new StringBody(fileName, ContentType.TEXT_PLAIN))
					.addPart("format", new StringBody(format, ContentType.TEXT_PLAIN))
					.addPart("package_id", new StringBody(packId, ContentType.TEXT_PLAIN))
					.addPart("url", new StringBody("path/to/save/dir", ContentType.TEXT_PLAIN))
//					.addPart("file", cbFile)
					.addPart("upload", cbFile);
			if (resourceId != null && !resourceId.isEmpty()) {
				multipart.addPart("id", new StringBody(resourceId, ContentType.TEXT_PLAIN));

				action = UPDATE_RESOURCE;
			}
			entity = multipart.build();
		}

		HttpEntity reqEntity = entity;
		String res = callCkan(ckanUrl, action, apiKey, reqEntity, true);
		return res;
	}
	
	public static JSONArray getResourceFields(String ckanUrl, String apiKey, String resourceId) {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
				.addPart("resource_id", new StringBody(resourceId, ContentType.TEXT_PLAIN))
				.addPart("limit", new StringBody("0", ContentType.TEXT_PLAIN));
		HttpEntity reqEntity = multipart.build();
		try {
			String action = DATASTORE_SEARCH;
			String res = callCkan(ckanUrl, action, apiKey, reqEntity, true);

			JSONObject json = new JSONObject(res.split("\n")[1]);
			return json.getJSONObject("result").getJSONArray("fields");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream getResourceFile(String resourceUrl) throws MalformedURLException, IOException {
		return new URL(resourceUrl).openStream();
	}

	public static ByteArrayInputStream getResourceFileAsByteArray(String resourceUrl) throws MalformedURLException, IOException {
		InputStream inputStream = getResourceFile(resourceUrl);

		byte[] buff = new byte[8000];

		int bytesRead = 0;

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		while ((bytesRead = inputStream.read(buff)) != -1) {
			bao.write(buff, 0, bytesRead);
		}

		byte[] data = bao.toByteArray();
		return new ByteArrayInputStream(data);
	}

	public static String pushInDatastore(String d4cUrl, String apiKey, String resourceId) throws Exception {
		String query = DATAPUSHER_PUSH_DATASTORE + resourceId;
		
		return sendGetMessage(d4cUrl, query, false);
	}

	@SuppressWarnings("deprecation")
	private static String callCkan(String ckanUrl, String method, String apiKey, HttpEntity reqEntity, boolean ignoreCertificates) throws Exception {
		CloseableHttpClient httpclient = null;
		if (ignoreCertificates) {
			final SSLConnectionSocketFactory sslsf;
			try {
			    sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
			            NoopHostnameVerifier.INSTANCE);
			} catch (NoSuchAlgorithmException e) {
			    throw new RuntimeException(e);
			}
	
			final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
			        .register("http", new PlainConnectionSocketFactory())
			        .register("https", sslsf)
			        .build();
	
			final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
			cm.setMaxTotal(100);
			httpclient = HttpClients.custom()
			        .setSSLSocketFactory(sslsf)
			        .setConnectionManager(cm)
			        .build();
		}
		else {
			httpclient = HttpClientBuilder.create().build();
		}
		
		HttpPost postRequest;
		try {
			postRequest = new HttpPost(ckanUrl + method);
			postRequest.setEntity(reqEntity);
			postRequest.addHeader("X-CKAN-API-Key", apiKey);
			if (reqEntity instanceof StringEntity) {
				postRequest.addHeader("Content-type", "application/json");
			}

			HttpResponse response = httpclient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			sb.append(statusCode + "\n");
			if (statusCode != 200) {
				hasError = true;
			}

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			httpclient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	public static String sendGetMessage(String urlStr, String servlet, boolean ignoreCertificates) throws Exception {
		if (ignoreCertificates) {
			fixUntrustCertificate();
		}
		
		String urlString = urlStr.endsWith("/") ? urlStr.substring(0, urlStr.length() -1 ) +  servlet : urlStr  + servlet;
		if(urlString.contains("?")) {
			urlString += "&cacheParam=" + new Object().hashCode();
		}
		URL url = new URL(urlString);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDefaultUseCaches(false);
		sock.setUseCaches(false);
//		sock.addRequestProperty("User-Agent", "PostmanRuntime/7.15.0");
//		sock.setDoInput(true);
//		sock.setDoOutput(true);
		sock.setRequestMethod("GET");
		sock.connect();

		String result = null;
		try {
			InputStream is = sock.getInputStream();

			BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			result = total.toString();
			is.close();
			sock.disconnect();

			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	private static void fixUntrustCertificate() throws KeyManagementException, NoSuchAlgorithmException {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// set the allTrusting verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}
