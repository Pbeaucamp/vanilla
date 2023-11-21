package bpm.vanilla.workplace.remote.internal;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.workplace.core.PlaceConstants;
import bpm.vanilla.workplace.core.services.VanillaPlaceService;
import bpm.vanilla.workplace.core.xstream.XmlAction;

/**
 * A Base communicator handling authentication on Workplace
 *
 */
public class HttpCommunicator {
	private String url;
//	private int connectionTimeOut = 5000;
	
	public HttpCommunicator(){
		try{
			//TODO: Modif timeOut
//			connectionTimeOut = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_COMPONENT_CONNECTION_TIMEOUT));
//			Logger.getLogger(getClass()).debug("HttpCommunicator Component Connection Timeout=" + connectionTimeOut);
		}catch(Exception ex){
			
		}
	}
	
	public void init(String workplaceUrl){
		if (workplaceUrl.endsWith("/"))
			url = workplaceUrl.substring(0, workplaceUrl.length() - 1);
		else 
			url = workplaceUrl;
	}
	
	protected String getUrl(){
		return url;
	}
	
	public String executeAction(XmlAction action, String message) throws Exception{
		if (action.getActionType() instanceof VanillaPlaceService.ActionType){
			return sendMessage(PlaceConstants.WORKPLACE_CONNECT_SERVLET, message);
		}
		throw new Exception("Unsupported ActionType " + action.getClass().getName()); 
	}

	protected String sendMessage(String servlet, String message) throws Exception{
		URL url = this.url.endsWith("/") ? new URL(this.url.substring(0, this.url.length() -1 ) +  servlet) : new URL(this.url  + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
			
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
			sock.disconnect();
			
			//error catching
			if (result.contains("<error>")){
				
				if (result.contains("<error><session>")){
					throw new Exception(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else{
					throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}
			}
			return result;
		}catch(Exception ex){
			throw ex;
		}

	}
}
