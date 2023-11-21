package bpm.aklabox.workflow.remote.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import bpm.aklabox.workflow.core.IAklaflowConstant;
import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.xstream.SessionExpiredException;
import bpm.aklabox.workflow.core.xstream.XmlAction;

public class HttpCommunicator extends AbstractRemoteAuthentifier {

	private String url;

	public void init(String vdmUrl, String login, String password, String sessionId) {
		if (vdmUrl.endsWith("/"))
			url = vdmUrl.substring(0, vdmUrl.length() - 1);
		else
			url = vdmUrl;

		super.init(login, password, sessionId);
	}

	protected String getUrl() {
		return url;
	}

	public String executeAction(XmlAction action, String message) throws Exception {
		if (action.getActionType() instanceof IAklaflowManager.ActionType) {
			return sendMessage(IAklaflowConstant.MOBILE_SERVLET, message);
		}

		throw new Exception("Operation non supported for now.");
	}

	public InputStream executeActionAsStream(XmlAction action, String message) throws Exception {
		if (action.getActionType() instanceof IAklaflowManager.ActionType) {
			return sendMessageForStream(IAklaflowConstant.MOBILE_SERVLET, message);
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
					throw new SessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
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

	private InputStream sendMessageForStream(String servlet, String message) throws Exception {
		URL url = getUrl().endsWith("/") ? new URL(this.getUrl().substring(0, this.getUrl().length() - 1) + servlet) : new URL(getUrl() + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		sock.setConnectTimeout(5000);
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

		String result = null;

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
			result = bos.toString();
			// error catching
			if (result.contains("<error>")) {
				throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
			}
			else {
				result = null;
			}

			return new ByteArrayInputStream(bos.toByteArray());
		} catch (Exception ex) {
			throw ex;
		}

	}

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
	}

}
