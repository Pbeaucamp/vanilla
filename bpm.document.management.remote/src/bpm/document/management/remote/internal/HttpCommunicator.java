package bpm.document.management.remote.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import bpm.document.management.core.IAkladematManager;
import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.xstream.SessionExpiredException;
import bpm.document.management.core.xstream.XmlAction;

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
		if (action.getActionType() instanceof IVdmManager.ActionType) {
			return sendMessage(AklaboxConstant.DOCUMENT_MANAGEMENT_SERVLET, message);
		}
		else if (action.getActionType() instanceof IAkladematManager.ActionType) {
			return sendMessage(AklaboxConstant.AKLADEMAT_MANAGEMENT_SERVLET, message);
		}
		else if (action.getActionType() instanceof IDocumentManager.ActionType) {
			return sendMessage(AklaboxConstant.DOCUMENT_SERVICE_SERVLET, message);
		}

		throw new Exception("Operation non supported for now.");
	}

	public InputStream executeActionAsStream(XmlAction action, String message) throws Exception {
		return executeActionAsStream(action, message, true);
	}

	public InputStream executeActionAsStream(XmlAction action, String message, boolean manageError) throws Exception {
		if (action.getActionType() instanceof IVdmManager.ActionType) {
			return sendMessageForStream(AklaboxConstant.DOCUMENT_MANAGEMENT_SERVLET, message, manageError);
		}
		else if (action.getActionType() instanceof IAkladematManager.ActionType) {
			return sendMessageForStream(AklaboxConstant.AKLADEMAT_MANAGEMENT_SERVLET, message, manageError);
		}

		throw new Exception("Operation non supported for now.");
	}

	public String executeActionThroughServlet(HashMap<String, String> params, InputStream inputStream) throws Exception {
		return sendMessageWithStream(AklaboxConstant.DOCUMENT_SERVICE_STREAM_SERVLET, params, inputStream);
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

			// This is complete bullshit. You know how much memory it takes
			// to do this ?
			// Do something else to handle exceptions, like send a message back
			// ?
			// If you have more than 50 documents in your Aklabox the cannot run
			// with 1GB of memory.
			// Which is a shame for a DOCUMENT platform.

			// InputStream is = sock.getInputStream();
			//
			// BufferedReader r = new BufferedReader(new InputStreamReader(is,
			// "UTF-8"));
			// StringBuilder total = new StringBuilder();
			// String line;
			// while ((line = r.readLine()) != null) {
			// total.append(line);
			// }
			// result = total.toString();
			InputStream is = sock.getInputStream();

			result = IOUtils.toString(is, "UTF-8");
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

	private InputStream sendMessageForStream(String servlet, String message, boolean manageError) throws Exception {
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
			if (manageError) {
				result = bos.toString();
				// error catching
				if (result.contains("<error>")) {
					throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
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

	protected String sendMessageWithStream(String servlet, HashMap<String, String> params, InputStream inputStream) throws Exception {
		URL url = getUrl().endsWith("/") ? new URL(this.getUrl().substring(0, this.getUrl().length() - 1) + servlet) : new URL(getUrl() + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		writeAdditionalHttpHeader(sock);
		if (params != null) {
			for (String key : params.keySet()) {
				sock.addRequestProperty(key, params.get(key));
			}
		}
		sock.setChunkedStreamingMode(1024 * 1024);
		sock.setConnectTimeout(5000);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");

		String result = null;
		try {
			// IOUtils.copy(inputStream, sock.getOutputStream());
			copyStream(inputStream, sock.getOutputStream());
			InputStream is = sock.getInputStream();

			result = IOUtils.toString(is, "UTF-8");
			is.close();

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

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {

	}

}
