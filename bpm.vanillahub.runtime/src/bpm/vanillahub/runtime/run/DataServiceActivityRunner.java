package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.DataServiceActivity;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeGrant;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeSecurity;
import bpm.vanillahub.core.beans.activities.attributes.ApiKeyProperties;
import bpm.vanillahub.core.beans.activities.attributes.ClientCredentialsProperties;
import bpm.vanillahub.core.beans.activities.attributes.DataServiceAttribute;
import bpm.vanillahub.core.beans.activities.attributes.URLProperties;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.vanillahub.runtime.utils.MapFluxHelper;
import bpm.vanillahub.runtime.utils.MapFluxHelper.WFSFormat;
import bpm.vanillahub.runtime.utils.OAuth2Helper;
import bpm.vanillahub.runtime.utils.Utils;
import bpm.vanillahub.runtime.utils.ZipHelper;
import bpm.workflow.commons.beans.Result;

public class DataServiceActivityRunner extends ActivityRunner<DataServiceActivity> {

	private String vanillaFilesPath;

	public DataServiceActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, DataServiceActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
	}

	@Override
	public void run(Locale locale) {
		String outputFileName = activity.getOutputName(parameters, variables);

		ByteArrayInputStream bis = getStreamFromService(locale, activity);
		if (bis != null) {
			result.setFileName(outputFileName);
			result.setInputStream(bis);

			IResultInformation infos = result.getInfosComp().get(TypeResultInformation.META_LINKS);
			List<MetaLink> links = infos != null && infos instanceof MetaLinksResultInformation ? ((MetaLinksResultInformation) infos).getLinks() : new ArrayList<MetaLink>();
			
			Meta metaApi = new Meta();
			metaApi.setKey("api");
			MetaLink linkApi = new MetaLink(metaApi);
			linkApi.setValue(new MetaValue("api", "true"));
			
			Meta metaType = new Meta();
			metaType.setKey("data4citizen-type");
			MetaLink linkType = new MetaLink(metaType);
			linkType.setValue(new MetaValue("data4citizen-type", "api"));

			links.add(linkApi);
			links.add(linkType);
			result.putInfoComp(TypeResultInformation.META_LINKS, new MetaLinksResultInformation(links));

			clearResources();
			result.setResult(Result.SUCCESS);
		}
		else if (result.getResult() == null || result.getResult() != Result.ERROR) {
			addError(Labels.getLabel(locale, Labels.ServiceDidNotSendData));

			result.setResult(Result.ERROR);
		}
	}

	private ByteArrayInputStream getStreamFromService(Locale locale, DataServiceActivity activity) {
		try {
			switch (activity.getTypeService()) {
			case QUANDL:
			case EBAY_FINDING:
				String encodeUrl = activity.buildDataUrl(parameters, variables);
				String response = sendMessage(encodeUrl);
				return new ByteArrayInputStream(response.getBytes());
			case URL:
			case API:
				return getStreamFromUrl(locale, activity);
			case WFS:
				return getStreamFromWfs(locale, activity);

			default:
				break;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToCallService));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	// private String buildYahooFinanceUrl(DataServiceActivity activity) throws
	// UnsupportedEncodingException {
	// YahooFinance yahooFinance = (YahooFinance) activity.getAttribute();
	//
	// TypeFinance typeFinance = yahooFinance.getTypeFinance();
	// String idsStr = yahooFinance.getIds(parameters, variables);
	// String propertiesStr = yahooFinance.getProperties(parameters, variables);
	//
	// StringBuilder builder = new StringBuilder();
	// switch (typeFinance) {
	// case COMPANIES:
	// builder.append(YahooFinance.COMPANIES_API_URL);
	// break;
	// case QUOTES:
	// builder.append(YahooFinance.QUOTES_API_URL);
	// break;
	// }
	//
	// builder.append(URLEncoder.encode(idsStr, "UTF-8"));
	//
	// if (propertiesStr != null && !propertiesStr.isEmpty()) {
	// builder.append("&f=");
	// builder.append(propertiesStr);
	// }
	// builder.append("&e=.csv");
	//
	// return builder.toString();
	// }
	//
	// private String buildYahooWeatherUrl(DataServiceActivity activity) throws
	// UnsupportedEncodingException {
	// YahooWeather yahooWeather = (YahooWeather) activity.getAttribute();
	//
	// TypeWeather typeWeather = yahooWeather.getTypeWeather();
	// String locationStr = yahooWeather.getLocation(parameters, variables);
	// String query = YahooWeather.QUERY.replace(YahooWeather.QUERY_TYPE,
	// typeWeather.getQuery());
	// query = query.replace(YahooWeather.QUERY_LOCATION, locationStr);
	//
	// StringBuilder builder = new StringBuilder(YahooWeather.API_URL);
	// builder.append(URLEncoder.encode(query, "UTF-8").replace("+", "%20"));
	// builder.append(YahooWeather.FORMAT);
	// builder.append(YahooWeather.ENV);
	// builder.append(URLEncoder.encode(YahooWeather.ENV_URL, "UTF-8"));
	//
	// return builder.toString();
	// }

	private String sendMessage(String serviceUrl) throws IOException {
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

		URL url = new URL(serviceUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestMethod("GET");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		InputStream is = httpConn.getInputStream();

		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		httpConn.disconnect();

		return result;
	}

	private ByteArrayInputStream getStreamFromUrl(Locale locale, DataServiceActivity activity) throws Exception {
		boolean unzip = activity.getAttribute() instanceof URLProperties ? ((URLProperties) activity.getAttribute()).isUnzip() : ((APIProperties) activity.getAttribute()).isUnzip();

		String url = activity.buildDataUrl(parameters, variables);

		addInfo(Labels.getLabel(locale, Labels.GetFileFromUrl) + " '" + url + "'");
		if (unzip) {
			String tempPath = vanillaFilesPath + "temp/";
			int uniqueIdentifier = new Object().hashCode();

			String filePath = tempPath + uniqueIdentifier + ".zip";
			String targetFolderPath = tempPath + uniqueIdentifier + "/";

			getResourceAsFile(url, filePath, "zip");
			// Unzip
			try (FileInputStream inputStream = new FileInputStream(filePath)) {
				ZipHelper.unzip(inputStream, targetFolderPath);

				File folder = new File(targetFolderPath);
				if (folder.exists()) {
					File[] files = folder.listFiles();
					if (files != null && files.length > 0) {
						// For now we only get the first file but we need to
						// manage loop later
						File file = files[0];

						try (FileInputStream zipFile = new FileInputStream(file)) {
							return Utils.getResourceAsStream(zipFile);
						} catch (Exception e) {
							e.printStackTrace();
							throw e;
						} finally {
							deleteDirectory(folder);
						}
					}
					else {
						throw new Exception("Zip does not contain any file.");
					}
				}
				else {
					throw new Exception("Unzip had an issue and could not be extracted with file '" + filePath + "'.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				deleteFile(filePath);
			}
		}
		else {
			String tempPath = vanillaFilesPath + "temp/";
			int uniqueIdentifier = new Object().hashCode();

			String filePath = tempPath + uniqueIdentifier + ".tmp";
			
			getResourceAsFile(url, filePath, "tmp");
			File f = new File(filePath);
			
//			try (InputStream inputStream = getResourceFile(url)) {
//				return Utils.getResourceAsStream(inputStream);
//			} catch (Exception e) {
//				throw new Exception(e.getMessage());
//			}

			String path = f.getAbsolutePath();
			result.setBigFile(true);
			return new ByteArrayInputStream(path.getBytes());
		}
	}

	private AuthDefinition manageSecurity(DataServiceAttribute attribute) throws IOException {
		if (activity.getAttribute() instanceof APIProperties) {
			APIProperties apiProperties = (APIProperties) activity.getAttribute();
			if (apiProperties.getTypeSecurity() == TypeSecurity.OAUTH20 && apiProperties.getTypeGrant() == TypeGrant.CLIENT_CREDENTIALS) {
				ClientCredentialsProperties clientCredentials = (ClientCredentialsProperties) apiProperties.getSecurity();
				String token = retrieveToken(clientCredentials);
				return new AuthDefinition("Authorization", "Bearer " + token);
			}
			else if (apiProperties.getTypeSecurity() == TypeSecurity.BASIC_AUTH) {
				//TODO:
			}
			else if (apiProperties.getTypeSecurity() == TypeSecurity.API_KEY) {
				ApiKeyProperties properties = (ApiKeyProperties) apiProperties.getSecurity();
				String apiKeyName = properties.getApiKeyName(parameters, variables);
				String apiKey = properties.getApiKey(parameters, variables);
				return new AuthDefinition(apiKeyName, apiKey);
			}
		}
		
		return null;
	}

	private String retrieveToken(ClientCredentialsProperties attribute) throws IOException {
		String accessTokenUrl = attribute.getAccessTokenURL(parameters, variables);
		String grantType = "client_credentials";
		String clientId = attribute.getClientId(parameters, variables);
		String clientSecret = attribute.getClientSecret(parameters, variables);
		String scope = attribute.getScope(parameters, variables);

		String[] scopes = scope != null && !scope.isEmpty() ? scope.split(" ") : null;

		OAuth2Helper oauthHelper = new OAuth2Helper();
		return oauthHelper.requestAccessToken(accessTokenUrl, grantType, clientId, clientSecret, Arrays.asList(scopes));
	}

	private ByteArrayInputStream getStreamFromWfs(Locale locale, DataServiceActivity activity) throws Exception {
		String url = activity.buildDataUrl(parameters, variables);
		
		APIProperties properties = activity.getAttribute() instanceof APIProperties ? (APIProperties) activity.getAttribute() : null;
		String layer = properties != null ? properties.getLayer(parameters, variables) : "";
		
		CkanResource resourceToDisplay = new CkanResource("", layer, "", url);

		addInfo("Retrieving data from WFS '" + resourceToDisplay.getUrl() + "'");
		WFSFormat selectedFormat = MapFluxHelper.getSelectedFormat(resourceToDisplay);
		logger.debug("Found format '" + selectedFormat + "'");
		try(InputStream inputStream = MapFluxHelper.retrieveJSONFromWFS(resourceToDisplay, selectedFormat.getValue())) {

//			logger.debug("Uploading data from WFS for resource '" + resourceToDisplay.getName() + "'");
//			D4CFormat resourceToDisplayFormat = getD4CFormat(selectedFormat);
			
			return Utils.getResourceAsStream(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Could not retrive WFS data from ressource '" + resourceToDisplay.getName() + "': " + e.getMessage());
			addError("Could not retrive WFS data from ressource '" + resourceToDisplay.getName() + "': " + e.getMessage());
			
			throw e;
		}
	}

	public void getResourceAsFile(String url, String filePath, String format) throws Exception {
		File targetFile = new File(filePath);
		InputStream inputStream = getResourceFile(url);
		java.nio.file.Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		IOUtils.closeQuietly(inputStream);
	}

	private InputStream getResourceFile(String url) throws Exception {
		AuthDefinition authDef = manageSecurity(activity.getAttribute());
		if (authDef != null) {
			HttpURLConnection sock = (HttpURLConnection) new URL(url).openConnection();

			sock.setRequestProperty(authDef.getHeader(), authDef.getAuth());
			sock.setRequestProperty("Content-Type", "application/json");
			sock.setRequestProperty("Accept", "application/json");
			sock.setRequestProperty("Origin", "vanilla");
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("GET");

			try {
				return sock.getInputStream();
			} catch (Exception ex) {
				throw ex;
			}
		}
		else {
			return downloadFile(url);
		}
	}
	
	private class AuthDefinition {
		
		private String header;
		private String auth;
		
		public AuthDefinition(String header, String auth) {
			this.header = header;
			this.auth = auth;
		}
		
		public String getHeader() {
			return header;
		}
		
		public String getAuth() {
			return auth;
		}
	}

	public static InputStream downloadFile(String fileURL) throws Exception {
//		URL url = new URL(fileURL);

		HttpGet get = new HttpGet(fileURL);
		get.setHeader("origin", "vanilla");

		HttpClient httpclient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		// HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = httpclient.execute(get);
		HttpEntity entity = response.getEntity();

		return entity.getContent();

        
//		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//		httpConn.setRequestProperty("Origin", "vanilla");
		
//		httpConn = followRedirect(httpConn, 0);
		
//		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
//		if (responseCode == HttpURLConnection.HTTP_OK) {
//			String disposition = httpConn.getHeaderField("Content-Disposition");
//			String contentType = httpConn.getContentType();
//			int contentLength = httpConn.getContentLength();
//
//			String fileName = "";
//			if (disposition != null) {
//				// extracts file name from header field
//				int index = disposition.indexOf("filename=");
//				if (index > 0) {
//					fileName = disposition.substring(index + 10, disposition.length() - 1);
//				}
//			}
//			else {
//				// extracts file name from URL
//				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
//			}
//
//			System.out.println("Content-Type = " + contentType);
//			System.out.println("Content-Disposition = " + disposition);
//			System.out.println("Content-Length = " + contentLength);
//			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
//			return httpConn.getInputStream();
//		}
//		else {
//			throw new Exception("No file to download. Server replied HTTP code: " + responseCode);
//		}
	}

//	private static HttpURLConnection followRedirect(HttpURLConnection conn, int index) throws Exception {
//		if (index > 5) {
//			throw new Exception("Too many redirect with url " + conn.getURL().toString());
//		}
//		
//		int status = conn.getResponseCode();
//	    if (status != HttpURLConnection.HTTP_OK) {
//	        if (status == HttpURLConnection.HTTP_MOVED_TEMP
//	            || status == HttpURLConnection.HTTP_MOVED_PERM
//	                || status == HttpURLConnection.HTTP_SEE_OTHER) {
//
//				String redirect = conn.getHeaderField("Location");
//				if (redirect != null){
//					conn = (HttpURLConnection) new URL(redirect).openConnection();
//				}
//		    	
//				return followRedirect(conn, index++);
//	        }
//	    }
//	    
//		return conn;
//	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}

	private boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	// private String downloadFileFromUrl(String url, String outputName) {
	// try (BufferedInputStream in = new BufferedInputStream(new
	// URL(url).openStream()); FileOutputStream fileOutputStream = new
	// FileOutputStream(outputName)) {
	// byte dataBuffer[] = new byte[1024];
	// int bytesRead;
	// while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
	// fileOutputStream.write(dataBuffer, 0, bytesRead);
	// }
	// } catch (IOException e) {
	// throw new Exception(e);
	// }
	// }

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void clearResources() {
	}
}
