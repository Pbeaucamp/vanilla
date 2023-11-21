package bpm.vanilla.platform.core.remote.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IArchiveManager;
import bpm.vanilla.platform.core.ICommentService;
import bpm.vanilla.platform.core.ICommunicationManager;
import bpm.vanilla.platform.core.IExcelManager;
import bpm.vanilla.platform.core.IExternalAccessibilityManager;
import bpm.vanilla.platform.core.IExternalManager;
import bpm.vanilla.platform.core.IGlobalManager;
import bpm.vanilla.platform.core.IImageManager;
import bpm.vanilla.platform.core.IMassReportMonitor;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.ISchedulerManager;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.IVanillaLoggingManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.IVanillaSystemManager.ActionType;
import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.HTMLFormComponent;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.PasswordException;
import bpm.vanilla.platform.core.exceptions.UserNotFoundException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent.GatewayHttpCommunicator;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime.ReportHttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;

/**
 * a Base communicator handling authentication on Vanilla It is used by the IVanillaApi Remote implementation
 * 
 * The writeAdditionalHttpHeader method does nothing. If you use this class to send some request to the VanillaDispatcher as you should do, you have to add at list the IVanillaComponentIdentifier.P_COMPONENT_NATURE httpHeader when implementing this method.
 * 
 * @author ludo
 * 
 */
public class HttpCommunicator extends AbstractRemoteAuthentifier {
	private String url;
	private int connectionTimeOut = 5000;
	private int authenticationTimeOut = 2000;

	public HttpCommunicator() {
		try {
			connectionTimeOut = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_COMPONENT_CONNECTION_TIMEOUT));
			Logger.getLogger(getClass()).debug("HttpCommunicator Component Connection Timeout=" + connectionTimeOut);
		} catch (Exception ex) {

		}

		try {
			authenticationTimeOut = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_AUTHENTICATION_TIMEOUT));
		} catch (Exception e) {
		}
	}

	public void init(String vanillaUrl, String login, String password) {
		// this.url = vanillaUrl;
		// ere
		if (vanillaUrl.endsWith("/"))
			url = vanillaUrl.substring(0, vanillaUrl.length() - 1);
		else
			url = vanillaUrl;

		super.init(login, password);
	}

	protected String getUrl() {
		return url;
	}

	/**
	 * Might be null
	 * 
	 * @return
	 */
	public String getCurrentSessionId() {
		return super.getSessionId();
	}

	public String executeAction(XmlAction action, String message, boolean isDispatching) throws Exception {
		if (!isDispatching) {
			if (action.getActionType() instanceof IVanillaSystemManager.ActionType) {
				return executeVanillaSystemAction((IVanillaSystemManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IRepositoryManager.ActionType) {
				return executeRepositoryAction((IRepositoryManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof HTMLFormComponent.ActionType) {
				return executeFormAction((HTMLFormComponent.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IPreferencesManager.ActionType) {
				return executePreferencesAction((IPreferencesManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IExternalAccessibilityManager.ActionType) {
				return executeExternalAccessibilityAction((IExternalAccessibilityManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IVanillaLoggingManager.ActionType) {
				return executeVanillaLoggingAction((IVanillaLoggingManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IVanillaSecurityManager.ActionType) {
				return executeVanillaSecurityAction((IVanillaSecurityManager.ActionType) action.getActionType(), message, false);
			}
			else if (action.getActionType() instanceof IUnitedOlapPreloadManager.ActionType) {
				return executeUnitedOlapPreloadAction((IUnitedOlapPreloadManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IVanillaComponentListenerService.ActionType) {
				return executeVanillaListenerAction((IVanillaComponentListenerService.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IImageManager.ActionType) {
				return executeImageManagerAction((IImageManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof IArchiveManager.ActionType) {
				return executeArchiveManagerAction((IArchiveManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof VanillaParameterComponent.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_PARAMETERS_PROVIDER_SERVLET, message);
			}
			else if (action.getActionType() instanceof ISchedulerManager.ActionType) {
				return sendMessage(ISchedulerManager.SERVLET_SCHEDULER, message);
			}
			else if (action.getActionType() instanceof IVanillaAccessRequestManager.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_ACCESS_MANAGER_SERVLET, message);
			}
			else if (action.getActionType() instanceof ReportHistoricComponent.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_HISTORIC_REPORT_SERVLET, message);
			}
			else if (action.getActionType() instanceof IGedComponent.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_GED_INDEX_SERVLET, message);
			}
			else if (action.getActionType() instanceof IMassReportMonitor.ActionType) {
				return sendMessage(IMassReportMonitor.SERVLET_MASS_REPORT_MONITOR, message);
			}
			else if (action.getActionType() instanceof IVanillaWebServiceComponent.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_WEBSERVICE_SERVLET, message);
			}
			else if (action.getActionType() instanceof ICommentService.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_COMMENT, message);
			}
			else if (action.getActionType() instanceof IExcelManager.ActionType) {
				return executeExcelAction((IExcelManager.ActionType) action.getActionType(), message);
			}
			else if (action.getActionType() instanceof ICommunicationManager.ActionType) {
				return sendMessage(VanillaConstants.VANILLA_FEEDBACK_SERVLET, message);
			}
			else if (action.getActionType() instanceof IResourceManager.ActionType) {
				return sendMessage(VanillaConstants.RESOURCE_MANAGER_SERVLET, message);
			}
			else if (action.getActionType() instanceof IGlobalManager.ActionType) {
				return sendMessage(VanillaConstants.GLOBAL_SERVLET, message);
			}
			else if (action.getActionType() instanceof IExternalManager.ActionType) {
				return sendMessage(VanillaConstants.EXTERNAL_SERVLET, message);
			}
			else {
				return sendMessage(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET, message);
			}
		}
		else {

			if (action.getActionType() instanceof ReportingComponent.ActionType || action.getActionType() instanceof IVanillaServerManager.ActionType && this instanceof ReportHttpCommunicator) {
				return sendMessage(ReportingComponent.REPORTING_SERVLET, message);
			}
			else if (action.getActionType() instanceof GatewayComponent.ActionType || action.getActionType() instanceof IVanillaServerManager.ActionType && this instanceof GatewayHttpCommunicator) {
				return sendMessage(GatewayComponent.GATEWAY_XTSREAM_SERVLET, message);
			}
			throw new Exception("Unsupported ActionType " + action.getClass().getName());
		}
	}

	private String executeArchiveManagerAction(bpm.vanilla.platform.core.IArchiveManager.ActionType actionType, String message) throws Exception {
		return sendMessage(VanillaConstants.ARCHIVE_MANAGER_SERVLET, message);
	}

	private String executeImageManagerAction(bpm.vanilla.platform.core.IImageManager.ActionType actionType, String message) throws Exception {
		return sendMessage(VanillaConstants.IMAGE_MANAGER_SERVLET, message);
	}

	public String executeAction(XmlAction action, String message, boolean isDispatching, boolean isTimedOut) throws Exception {
		if (action.getActionType() instanceof IVanillaSecurityManager.ActionType) {
			return executeVanillaSecurityAction((IVanillaSecurityManager.ActionType) action.getActionType(), message, isTimedOut);
		}
		throw new Exception("Unsupported ActionType " + action.getClass().getName());
	}

	public InputStream executeActionAsStream(XmlAction action, String message, boolean isDispatching) throws Exception {
		if (!isDispatching) {
			return sendMessageForStream(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET, message, true);
		}
		else {
			if (action.getActionType() instanceof ReportingComponent.ActionType) {
				return sendMessageForStream(ReportingComponent.REPORTING_SERVLET, message, true);
			}
			else if (action.getActionType() instanceof GatewayComponent.ActionType) {
				return sendMessageForStream(GatewayComponent.GATEWAY_XTSREAM_SERVLET, message, true);
			}
		}

		throw new Exception("Unsupported ActionType " + action.getClass().getName());
	}

	public InputStream executeActionAsStream(String servletUrl, String message) throws Exception {
		return sendMessageForStream(servletUrl, message, true);
	}

	public InputStream executeActionForBigStream(String servletUrl, String message) throws Exception {
		return sendMessageForBigStream(servletUrl, message);
	}

	private String executeVanillaListenerAction(IVanillaComponentListenerService.ActionType action, String message) throws Exception {
		// if (action ==
		// IVanillaComponentListenerService.ActionType.REMOVE_LISTENER){
		// return sendMessage(VanillaConstants.VANILLA_SYSTEM_MANAGER_SERVLET,
		// message, connectionTimeOut);
		// }
		return sendMessage(VanillaConstants.VANILLA_LISTENERS_SERVLET, message);
	}

	private String executeUnitedOlapPreloadAction(IUnitedOlapPreloadManager.ActionType action, String message) throws Exception {
		return sendMessage(VanillaConstants.VANILLA_UNITED_OLAP_PRELOAD_SERVLET, message);
	}

	private String executeVanillaSecurityAction(IVanillaSecurityManager.ActionType action, String message, boolean isTimedOut) throws Exception {
		if (isTimedOut) {
			return sendMessage(VanillaConstants.VANILLA_SECURITY_MANAGER_SERVLET, message, authenticationTimeOut);
		}
		else {
			return sendMessage(VanillaConstants.VANILLA_SECURITY_MANAGER_SERVLET, message);
		}
	}

	private String executeVanillaLoggingAction(IVanillaLoggingManager.ActionType action, String message) throws Exception {

		return sendMessage(VanillaConstants.VANILLA_LOGGING_MANAGER_SERVLET, message);
	}

	private String executeExternalAccessibilityAction(IExternalAccessibilityManager.ActionType action, String message) throws Exception {
		return sendMessage(VanillaConstants.VANILLA_EXTERNAL_ACCESSIBILLITY_MANAGER_SERVLET, message);
	}

	private String executePreferencesAction(IPreferencesManager.ActionType action, String message) throws Exception {
		return sendMessage(VanillaConstants.VANILLA_PREFERENCES_MANAGER_SERVLET, message);
	}

	private String executeRepositoryAction(IRepositoryManager.ActionType action, String message) throws Exception {
		return sendMessage(VanillaConstants.VANILLA_REPOSITORY_MANAGER_SERVLET, message);
	}

	private String executeVanillaSystemAction(ActionType action, String message) throws Exception {

		return sendMessage(VanillaConstants.VANILLA_SYSTEM_MANAGER_SERVLET, message);
	}

	private String executeFormAction(HTMLFormComponent.ActionType action, String message) throws Exception {
		return sendMessage(HTMLFormComponent.LIST_FORMS_SERVLET, message);
	}

	private String executeExcelAction(IExcelManager.ActionType action, String message) throws Exception {
		return sendMessage(VanillaConstants.VANILLA_EXCEL_MANAGER_SERVLET, message);
	}

	public String executeActionThroughServlet(HashMap<String, String> params, InputStream inputStream) throws Exception {
		return sendMessageWithStream(VanillaConstants.VANILLA_BIG_FILE_GED_SERVLET, params, inputStream);
	}

	protected String sendMessage(String servlet, String message) throws Exception {

		String newUrl = url;
		try {
			// rewrite the url to localhost if necessary
			String rewrite = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL_LOCALHOST_REWRITE);
			if (rewrite != null && Boolean.parseBoolean(rewrite)) {
				String baseUrlRuntime = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
				if(url.startsWith(baseUrlRuntime)) {
					// extract the port
					String port = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
					String protocole = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
					if (port.equals("")) {
						newUrl = protocole + "localhost" + getUrlEnding(url);
					}
					else {
						newUrl = protocole + "localhost:" + port + getUrlEnding(url);
					}
				}
				Logger.getLogger(getClass()).trace("baseUrlComp : " + url + " baseUrlRuntime : " + baseUrlRuntime);
				Logger.getLogger(getClass()).trace("url changed from : " + url + " to : " + newUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		URL url = newUrl.endsWith("/") ? new URL(newUrl.substring(0, newUrl.length() - 1) + servlet) : new URL(newUrl + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
//		sock.setReadTimeout(0);

		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		// sock.setConnectTimeout(connectionTimeOut);
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

			result = IOUtils.toString(is, "UTF-8");
			is.close();
//			sock.disconnect();

			extractSessionId(sock);
			// error catching
			if (result.contains("<error>")) {

				try {
					if (result.contains("<error><session>")) {
						throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
					}
					else if (result.contains("<error><password>")) {
						throw new PasswordException();
					}
					else if (result.contains("<error><user>")) {
						throw new UserNotFoundException(result.replace("<error><user>", "").replace("</error></user>", ""));
					}
					else {
						throw new VanillaException(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
					}
				} catch(Exception e) {
					throw new VanillaException(result);
				}

			}
			return result;
		} catch (Exception ex) {
			// ex.printStackTrace();
			//
			// result = IOUtils.toString(sock.getErrorStream(), "UTF-8");
			// throw new Exception( result.substring(result.indexOf("<body>") +
			// 6, result.indexOf("</body>")));
			throw ex;
		}

	}

	public String extractPort(String url) {
		int firstSlash = url.indexOf("/");
		int secondSlash = url.indexOf("/", firstSlash + 2);

		try {
			return url.substring(firstSlash, secondSlash > -1 ? secondSlash : url.length()).split(":")[1];
		} catch (Exception e) {
			return "";
		}
	}

	public String getProtocole(String url) {
		int firstSlash = url.indexOf("/");

		return url.substring(0, firstSlash + 1) + "/";
	}

	public String getUrlEnding(String url) {
		int firstSlash = url.indexOf("/");
		int secondSlash = url.indexOf("/", firstSlash + 2);

		return url.substring(secondSlash > -1 ? secondSlash : url.length(), url.length());
	}

	private String sendMessage(String servlet, String message, int timeOut) throws Exception {
		String newUrl = url;
		try {
			// rewrite the url to localhost if necessary
			String rewrite = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL_LOCALHOST_REWRITE);
			if (rewrite != null && Boolean.parseBoolean(rewrite)) {
				String baseUrlRuntime = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
				if(url.startsWith(baseUrlRuntime)) {
					// extract the port
					String port = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
					String protocole = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
					if (port.equals("")) {
						newUrl = protocole + "localhost" + getUrlEnding(url);
					}
					else {
						newUrl = protocole + "localhost:" + port + getUrlEnding(url);
					}
				}
				Logger.getLogger(getClass()).trace("baseUrlComp : " + url + " baseUrlRuntime : " + baseUrlRuntime);
				Logger.getLogger(getClass()).trace("url changed from : " + url + " to : " + newUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		URL url = newUrl.endsWith("/") ? new URL(newUrl.substring(0, newUrl.length() - 1) + servlet) : new URL(newUrl + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setConnectTimeout(timeOut);
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

			result = IOUtils.toString(is, "UTF-8");
			is.close();
			sock.disconnect();

			extractSessionId(sock);
			// error catching
			if (result.contains("<error>")) {

				if (result.contains("<error><session>")) {
					throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else if (result.contains("<error><password>")) {
					throw new PasswordException();
				}
				else if (result.contains("<error><user>")) {
					throw new UserNotFoundException(result.replace("<error><user>", "").replace("</error></user>", ""));
				}
				else {
					throw new VanillaException(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}

			}
			return result;
		} catch (Exception ex) {
			// ex.printStackTrace();
			//
			// result = IOUtils.toString(sock.getErrorStream(), "UTF-8");
			// throw new Exception( result.substring(result.indexOf("<body>") +
			// 6, result.indexOf("</body>")));
			throw ex;
		}

	}

	protected String sendMessageWithStream(String servlet, HashMap<String, String> params, InputStream inputStream) throws Exception {

		String newUrl = url;
		try {
			// rewrite the url to localhost if necessary
			String rewrite = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL_LOCALHOST_REWRITE);
			if (rewrite != null && Boolean.parseBoolean(rewrite)) {
				// extract the port
				String port = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
				String protocole = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
				if (port.equals("")) {
					newUrl = protocole + "localhost" + getUrlEnding(url);
				}
				else {
					newUrl = protocole + "localhost:" + port + getUrlEnding(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		URL url = newUrl.endsWith("/") ? new URL(newUrl.substring(0, newUrl.length() - 1) + servlet) : new URL(newUrl + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		if (params != null) {
			for (String key : params.keySet()) {
				sock.addRequestProperty(key, URLEncoder.encode(params.get(key), "UTF-8"));
			}
		}
		sock.setChunkedStreamingMode(1024 * 1024);
		sock.setConnectTimeout(connectionTimeOut);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		String result = null;
		try {
//			IOUtils.copy(inputStream, sock.getOutputStream());
			copyStream(inputStream, sock.getOutputStream());
			InputStream is = sock.getInputStream();

			result = IOUtils.toString(is, "UTF-8");
			is.close();

			// error catching
			if (result.contains("<error>")) {
				if (result.contains("<error><session>")) {
					throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else if (result.contains("<error><password>")) {
					throw new PasswordException();
				}
				else if (result.contains("<error><user>")) {
					throw new UserNotFoundException(result.replace("<error><user>", "").replace("</error></user>", ""));
				}
				else {
					throw new VanillaException(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}
			}
			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	private InputStream sendMessageForStream(String servlet, String message, boolean manageError) throws Exception {
		URL url = getUrl().endsWith("/") ? new URL(this.getUrl().substring(0, this.getUrl().length() - 1) + servlet) : new URL(getUrl() + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setConnectTimeout(connectionTimeOut);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		if (message != null) {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
			StringBuffer toSend = new StringBuffer();
			toSend.append(message);

			pw.write(toSend.toString());
			pw.close();
		}

		// store result in Stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			InputStream is = sock.getInputStream();

			byte[] buf = new byte[1024];
			int sz = 0;
			while ((sz = is.read(buf)) >= 0) {
				bos.write(buf, 0, sz);
			}

			is.close();
			sock.disconnect();

			extractSessionId(sock);
			if (manageError) {
				String result = bos.toString();
				// error catching
				if (result.contains("<error>")) {
	
					if (result.contains("<error><session>")) {
						throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
					}
					else if (result.contains("<error><password>")) {
						throw new PasswordException();
					}
					else if (result.contains("<error><user>")) {
						throw new UserNotFoundException(result.replace("<error><user>", "").replace("</error></user>", ""));
					}
					else {
						throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
					}
				}
				else {
					result = null;
				}
			}

			return new ByteArrayInputStream(bos.toByteArray());
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * We canno't manage error for big file, we send the FileInputStream directly
	 * 
	 * @param servlet
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private InputStream sendMessageForBigStream(String servlet, String message) throws Exception {
		URL url = getUrl().endsWith("/") ? new URL(this.getUrl().substring(0, this.getUrl().length() - 1) + servlet) : new URL(getUrl() + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setConnectTimeout(connectionTimeOut);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		if (message != null) {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
			StringBuffer toSend = new StringBuffer();
			toSend.append(message);

			pw.write(toSend.toString());
			pw.close();
		}

		try {
			extractSessionId(sock);
			return sock.getInputStream();
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
	}
}
