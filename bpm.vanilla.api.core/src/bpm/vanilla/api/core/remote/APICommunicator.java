package bpm.vanilla.api.core.remote;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.api.core.APIAction;
import bpm.vanilla.api.core.IAPIManager;
import bpm.vanilla.api.core.JSONHelper;
import bpm.vanilla.api.core.remote.internal.AbstractRemoteAuthentifier;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * a Base communicator handling authentication on Vanilla It is used by the IVanillaApi Remote implementation
 * 
 */
public class APICommunicator extends AbstractRemoteAuthentifier {
	
	private String url;

	public APICommunicator() {
		
		//TODO: Change security
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
//		String rootLogin = conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
//		String rootPassword = conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String vanillaUrl = conf.getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		init(vanillaUrl);
	}

	public void init(String vanillaUrl, String sessionId) {
		if (vanillaUrl.endsWith("/"))
			url = vanillaUrl.substring(0, vanillaUrl.length() - 1);
		else
			url = vanillaUrl;

		super.init(sessionId);
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

	public String executeAction(APIAction action) throws Exception {
		String json = JSONHelper.transformToJSon(action);
		
		String newUrl = url;
		
		URL url = newUrl.endsWith("/") ? new URL(newUrl.substring(0, newUrl.length() - 1) + IAPIManager.SERVLET_API) : new URL(newUrl + IAPIManager.SERVLET_API);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		// sock.setConnectTimeout(connectionTimeOut);
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		pw.write(json);
		pw.close();
		
		String result = null;
		try {
			InputStream is = sock.getInputStream();

			result = IOUtils.toString(is, "UTF-8");
			is.close();

			extractSessionId(sock);

			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public InputStream executeStreamAction(APIAction action) throws Exception {
		String json = JSONHelper.transformToJSon(action);
		
		String newUrl = url;
		
		URL url = newUrl.endsWith("/") ? new URL(newUrl.substring(0, newUrl.length() - 1) + IAPIManager.STREAM_API) : new URL(newUrl + IAPIManager.STREAM_API);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		// sock.setConnectTimeout(connectionTimeOut);
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		pw.write(json);
		pw.close();
		
		try {
			return sock.getInputStream();
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
	}
}
