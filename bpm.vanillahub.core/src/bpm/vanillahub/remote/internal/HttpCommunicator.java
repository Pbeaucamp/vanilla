package bpm.vanillahub.remote.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.workflow.commons.remote.IAdminManager;
import bpm.workflow.commons.remote.internal.AbstractRemoteAuthentifier;
import bpm.workflow.commons.remote.internal.RemoteConstants;

public class HttpCommunicator extends AbstractRemoteAuthentifier {

	private String url;

	public void init(String vanillaUrl, String sessionId, Locale locale) {
		if (vanillaUrl.endsWith("/"))
			url = vanillaUrl.substring(0, vanillaUrl.length() - 1);
		else
			url = vanillaUrl;

		super.init(sessionId, locale);
	}

	protected String getUrl() {
		return url;
	}

	public String executeAction(XmlAction action, String message) throws Exception {
		if (action.getActionType() instanceof IAdminManager.ActionType) {
			return sendMessage(Constants.SERVLET_ADMIN_MANAGER, message);
		}
		else if (action.getActionType() instanceof IHubWorkflowManager.ActionType) {
			return sendMessage(IHubWorkflowManager.VANILLA_HUB_SERVLET, message);
		}
		else if (action.getActionType() instanceof IHubResourceManager.ActionType) {
			return sendMessage(IHubResourceManager.VANILLA_HUB_SERVLET, message);
		}

		throw new Exception("Operation non supported for now.");
	}

	protected String sendMessage(String servlet, String message) throws Exception {
		URL url = this.url.endsWith("/") ? new URL(this.url.substring(0, this.url.length() - 1) + servlet) : new URL(this.url + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);

		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
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

			extractSessionId(sock);

			// error catching
			if (result.contains("<error>")) {

				if (result.contains("<error><session>")) {
					throw new Exception(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else if (result.contains("<error><password>")) {
					throw new Exception(result);
				}
				else if (result.contains("<error><user>")) {
					throw new Exception(result.replace("<error><user>", "").replace("</error></user>", ""));
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
		if (getLocale() != null) {
			sock.setRequestProperty(RemoteConstants.HTTP_HEADER_LOCALE, getLocale().getLanguage());
		}
	}

}
