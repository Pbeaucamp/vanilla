package bpm.geoloc.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

public class NodeHelper {

	private static final String PARAM_PRECLUSTER = "precluster?id=";
	private static final String PARAM_SEPARATOR = "separator=";
	private static final String PARAM_ENCODING = "encoding=";
	private static final String PARAM_COL_COORDINATE = "colCoordinate=";
	private static final String PARAM_COL_COORDINATE_SEPARATOR = "colCoordinateSeparator=";
	
	public static void callNodeAndUploadResource(String nodeUrl, String nodePath, String ckanUrl, String apiKey, String packageId, char separator, String encoding, String columnCoordinate, String columnCoordinateSeparator, boolean uploadFile) throws Exception {
		//We get the package name if we can
		String packName = CkanHelper.getPackageName(ckanUrl, packageId);
		
		nodePath = nodePath.endsWith("/") ? nodePath : nodePath + "/";

		// We create the GeoJson and the clusters
		/*String result = */callNode(nodeUrl, packageId, separator, encoding, columnCoordinate, columnCoordinateSeparator);

		// We upload the GeoJson file
		String geojsonFile = nodePath + packName + "/" + packName + ".geojson";
		
		//We test if the file is created or we wait until it is (we set a limit of 10 min by default)
		boolean timeoutReached = false;
		
		int timeoutMinutes = WaitHelper.TIMEOUT_DEFAULT;

		System.out.println("Waiting for geojson file '" + geojsonFile + "' to be created for " + timeoutMinutes + " min.");
		
		Date timeout = WaitHelper.getTimoutDate(timeoutMinutes);
		while(!timeoutReached) {

			TimeUnit.SECONDS.sleep(10);
			
			File file = new File(geojsonFile);
			if (file.exists()) {
				System.out.println("Geojson was created.");
				if (uploadFile) {
					System.out.println("We upload it on CKAN.");
					CkanHelper.uploadCkanFile(ckanUrl, apiKey, packName + ".geojson", "geojson", geojsonFile, packName, null);
				}
				break;
			}
			
			Date currentDate = new Date();
			if (currentDate.after(timeout)) {
				timeoutReached = true;
				System.out.println("Geojson could not be created in " + timeoutMinutes + ". We were not able to upload it.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static String callNode(String nodeUrl, String packageName, char separator, String encoding, String columnCoordinate, String columnCoordinateSeparator) throws Exception {
//		final SSLConnectionSocketFactory sslsf;
//		try {
//		    sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
//		            NoopHostnameVerifier.INSTANCE);
//		} catch (NoSuchAlgorithmException e) {
//		    throw new RuntimeException(e);
//		}
//
//		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//		        .register("http", new PlainConnectionSocketFactory())
//		        .register("https", sslsf)
//		        .build();
//
//		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
//		cm.setMaxTotal(100);
//		CloseableHttpClient httpclient = HttpClients.custom()
//		        .setSSLSocketFactory(sslsf)
//		        .setConnectionManager(cm)
//		        .build();
		
		StringBuffer buf = new StringBuffer();
		buf.append(nodeUrl + PARAM_PRECLUSTER + packageName);
		buf.append("&" + PARAM_SEPARATOR + separator);
		if (encoding != null && !encoding.isEmpty()) {
			buf.append("&" + PARAM_ENCODING + encoding);
		}
		if (columnCoordinate != null && !columnCoordinate.isEmpty()) {
			buf.append("&" + PARAM_COL_COORDINATE + columnCoordinate);
		}
		if (columnCoordinateSeparator != null && !columnCoordinateSeparator.isEmpty()) {
			buf.append("&" + PARAM_COL_COORDINATE_SEPARATOR + columnCoordinateSeparator);
		}
		
		CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

		HttpGet getRequest;
		try {
			getRequest = new HttpGet(buf.toString());

			HttpResponse response = httpClient.execute(getRequest);
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

			httpClient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}
