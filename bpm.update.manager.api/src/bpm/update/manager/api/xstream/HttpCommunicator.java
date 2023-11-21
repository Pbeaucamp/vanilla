package bpm.update.manager.api.xstream;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.update.manager.api.IRuntimeUpdateManager;
import bpm.update.manager.api.IUpdateManager;
import bpm.update.manager.api.utils.Constants;

public class HttpCommunicator extends AbstractRemoteAuthentifier {
	private String url;
	private int connectionTimeOut = 5000;

	public HttpCommunicator() {
		try {
			Logger.getLogger(getClass()).debug("HttpCommunicator Component Connection Timeout=" + connectionTimeOut);
		} catch (Exception ex) {

		}
	}

	public void init(String serverUrl, String login, String password) {
		// this.url = vanillaUrl;
		// ere
		if (serverUrl.endsWith("/"))
			url = serverUrl.substring(0, serverUrl.length() - 1);
		else
			url = serverUrl;

		super.init(login, password);
	}

	public void init(String serverUrl) {
		// this.url = vanillaUrl;
		// ere
		if (serverUrl.endsWith("/"))
			url = serverUrl.substring(0, serverUrl.length() - 1);
		else
			url = serverUrl;
	}

	protected String getUrl() {
		return url;
	}

	public String executeAction(XmlAction action, String message) throws Exception {
		if (action.getActionType() instanceof IRuntimeUpdateManager.ActionType) {
			return sendMessage(Constants.UPDATE_MANAGER_SERVLET, message);
		}
		else if (action.getActionType() instanceof IUpdateManager.ActionType) {
			return sendMessage(Constants.UPDATE_MANAGER_SERVLET, message);
		}
		throw new Exception("Unsupported ActionType " + action.getClass().getName());
	}

	protected String sendMessage(String servlet, String message) throws Exception {
		URL url = this.url.endsWith("/") ? new URL(this.url.substring(0, this.url.length() - 1) + servlet) : new URL(this.url + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		// send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		StringBuffer toSend = new StringBuffer();
		toSend.append(message);

		pw.write(toSend.toString());
		pw.close();
		String result = null;
		try {
			InputStream is = sock.getInputStream();

			result = IOUtils.toString(is, "UTF-8");
			is.close();
			sock.disconnect();

			// error catching
			if (result.contains("<error>")) {

				if (result.contains("<error><session>")) {
					throw new Exception(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else {
					throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}
			}
			return result;
		} catch (Exception ex) {
			throw ex;
		}

	}

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
	}
}
