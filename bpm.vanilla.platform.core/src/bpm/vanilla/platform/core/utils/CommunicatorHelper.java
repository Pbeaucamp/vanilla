package bpm.vanilla.platform.core.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CommunicatorHelper {

	public static String sendPostMessage(String urlStr, String servlet, String message) throws Exception {
		return sendPostMessage(urlStr, servlet, message, true);
	}

	public static String sendPostMessage(String urlStr, String servlet, String message, boolean ignoreCertificates) throws Exception {
		if (ignoreCertificates) {
			fixUntrustCertificate();
		}
		
		URL url = urlStr.endsWith("/") ? new URL(urlStr.substring(0, urlStr.length() - 1) + servlet) : new URL(urlStr + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		sock.setRequestProperty("Content-type", "application/json;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));

		pw.write(message);
		pw.close();
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

	public static String sendGetMessageWithAuth(String urlStr, String servlet, String token) throws Exception {
		return sendGetMessageWithAuth(urlStr, servlet, token, false);
	}

	public static String sendGetMessageWithAuth(String urlStr, String servlet, String token, boolean ignoreCertificates) throws Exception {
		if (ignoreCertificates) {
			fixUntrustCertificate();
		}
		
		URL url = urlStr.endsWith("/") ? new URL(urlStr.substring(0, urlStr.length() - 1) + servlet) : new URL(urlStr + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		sock.setRequestProperty("Authorization", "Bearer " + token);
		sock.setRequestProperty("Content-Type", "application/json");
		// e.g. bearer token=
		// eyJhbGciOiXXXzUxMiJ9.eyJzdWIiOiPyc2hhcm1hQHBsdW1zbGljZS5jb206OjE6OjkwIiwiZXhwIjoxNTM3MzQyNTIxLCJpYXQiOjE1MzY3Mzc3MjF9.O33zP2l_0eDNfcqSQz29jUGJC-_THYsXllrmkFnk85dNRbAw66dyEKBP5dVcFUuNTA8zhA83kk3Y41_qZYx43T
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("GET");

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
	
	public static String sendGetMessage(String urlStr, String servlet) throws Exception {
		return sendGetMessage(urlStr, servlet, null, true);
	}
	
	public static String sendGetMessage(String urlStr, String servlet, String apiKey) throws Exception {
		return sendGetMessage(urlStr, servlet, apiKey, true);
	}
	
	private static String sendGetMessage(String urlStr, String servlet, String apiKey, boolean ignoreCertificates) throws Exception {
		return sendGetMessage(urlStr, servlet, apiKey, ignoreCertificates, true);
	}
	
	public static String sendGetMessage(String urlStr, String servlet, boolean ignoreCertificates, boolean cacheParam) throws Exception {
		return sendGetMessage(urlStr, servlet, null, ignoreCertificates, cacheParam);
	}

	private static String sendGetMessage(String urlStr, String servlet, String apiKey, boolean ignoreCertificates, boolean cacheParam) throws Exception {
//		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		
		if (ignoreCertificates) {
			fixUntrustCertificate();
		}
		
		String urlString = urlStr.endsWith("/") ? urlStr.substring(0, urlStr.length() -1 ) +  servlet : urlStr  + servlet;
		if (urlString.contains("?") && cacheParam) {
			urlString += "&cacheParam=" + new Object().hashCode();
		}
		
		URL url = new URL(urlString);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDefaultUseCaches(false);
		sock.setUseCaches(false);
		if (apiKey != null) {
			sock.setRequestProperty ("Authorization", apiKey);
		}
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
