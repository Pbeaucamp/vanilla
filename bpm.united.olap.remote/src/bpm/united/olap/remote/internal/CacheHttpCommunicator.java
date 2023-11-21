package bpm.united.olap.remote.internal;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.remote.internal.AbstractRemoteAuthentifier;
import bpm.vanilla.platform.core.xstream.XmlAction;

public class CacheHttpCommunicator extends AbstractRemoteAuthentifier{

	private String url;
	
	public CacheHttpCommunicator(String url) {
		if (url.endsWith("/"))
			this.url = url.substring(0, url.length() - 1);
		else 
			this.url = url;
	}
	
	public String executeAction(XmlAction action, String message) throws Exception {
		return sendMessage("unitedolap/cacheManagerService", message);
//		switch(action.getActionType()) {
//			case ActionType.LOAD:
//			case ActionType.UNLOAD:
//			case ActionType.SUBMEMBERS:
//			case ActionType.SEARCHDIMS:
//			case ActionType.FIND_SCHEMA:
//			case ActionType.EXPLORE:
//			case ActionType.DISTINCTVALUES:
//				return sendMessage("unitedolap/modelService", message);
//				
//			case ActionType.EXECUTE_QUERY:
//			case ActionType.DRILLTHROUGH:
//			case ActionType.PRELOAD:
//				return sendMessage("unitedolap/runtimeService", message);
//			case ActionType.CLEAR_DISK_CACHE:
//			case ActionType.DISK_ENTRY:
//			case ActionType.DISK_KEYS:
//			case ActionType.DISK_STATS:
//			case ActionType.CLEAR_MEMORY_CACHE:
//			case ActionType.MEMORY_STATS:
//			case ActionType.APPEND_TO_CACHE:
//			case ActionType.LOAD_CACHE_ENTRY:
//				return sendMessage("unitedolap/cacheManagerService", message);
//		}
//		throw new Exception("XMLActionType " + action.getActionType() + " not supported") ;
	}
 	
	private String sendMessage(String servlet, String message) throws Exception {
		URL url = new URL(this.url + "/" + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
//		writeAdditionalHttpHeader(sock);
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
			
		//send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		StringBuffer toSend = new StringBuffer();
		toSend.append(message);
		
		pw.write(toSend.toString());
		pw.close();
		String result = null;
		try{
			InputStream is = sock.getInputStream();
		
			result = IOUtils.toString(is, "UTF-8");
			is.close();
			
			//error catching
			if (result.contains("<error>")){
				throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
			}
			extractSessionId(sock);
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			
			result = IOUtils.toString(sock.getErrorStream()); 
			throw new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
		}
	}
	
	public void init(String url, String login, String password) {
		if (url.endsWith("/"))
			this.url = url.substring(0, url.length() - 1);
		else 
			this.url = url;
		
		super.init(login, password);
	}
	
	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
		sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_UNITEDOLAP);
		
	}
}
