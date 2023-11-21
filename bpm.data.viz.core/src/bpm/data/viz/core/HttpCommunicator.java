package bpm.data.viz.core;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.xstream.XmlAction;

public class HttpCommunicator extends AbstractRemoteAuthentifier {

	private IRepositoryApi repositoryConnection;
	
	public void init(IRepositoryApi repositoryConnection) {
		this.repositoryConnection = repositoryConnection;
		super.init(repositoryConnection.getContext().getVanillaContext().getLogin(), repositoryConnection.getContext().getVanillaContext().getPassword());
	}

	public String executeAction(XmlAction action, String message) throws Exception {
		return sendMessage(IDataVizComponent.DATA_VIZ_SERVLET, message);
	}

	protected String sendMessage(String servlet, String message) throws Exception {
		
		String runtimeUrl = repositoryConnection.getContext().getVanillaContext().getVanillaUrl();
		
		URL url = runtimeUrl.endsWith("/") ? new URL(runtimeUrl.substring(0, runtimeUrl.length() - 1) + servlet) : new URL(runtimeUrl + servlet);
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
			result = IOUtils.toString(is, "UTF-8");
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
		//write Group id header
		if (repositoryConnection.getContext().getGroup() != null){
			sock.setRequestProperty(IRepositoryApi.HTTP_HEADER_GROUP_ID, repositoryConnection.getContext().getGroup().getId() + "");
		}
		
		//write RepositoryDefinitionHeader
		sock.setRequestProperty(IRepositoryApi.HTTP_HEADER_REPOSITORY_ID, repositoryConnection.getContext().getRepository().getId() + "");
	}



}

