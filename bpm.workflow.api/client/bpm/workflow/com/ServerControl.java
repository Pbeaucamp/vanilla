package bpm.workflow.com;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.repository.RepositoryItem;


public class ServerControl {

	
	
	
	public static void deploy(String vanillaUrl, RepositoryItem item) throws Exception{
				
		String url = vanillaUrl + "/Workflow?action=deploy&itemId=" + item.getId() ;
		sendToServer(url);
	}
	
	public static void undeploy(String vanillaUrl, RepositoryItem item) throws Exception{
		String url = vanillaUrl + "/Workflow?action=undeploy&itemId=" + item.getId() ;
		sendToServer(url);
	}
	
	public static void start(String vanillaUrl, RepositoryItem item) throws Exception{
		String url = vanillaUrl + "/Workflow?action=start&itemId=" + item.getId() ;
		sendToServer(url);
	}
	
	
	protected static void sendToServer(String fullUrl) throws Exception{
		URL url = new URL(fullUrl);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		
		InputStream is = sock.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();

	}
	

}
