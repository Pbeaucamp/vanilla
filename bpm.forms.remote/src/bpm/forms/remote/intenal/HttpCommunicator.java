package bpm.forms.remote.intenal;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.vanilla.platform.core.components.IFormComponent;




public class HttpCommunicator {

	private String url;
	
	public HttpCommunicator(String url) {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
			
		this.url = url;
	}
	
	
	public String executeAction(XmlAction action, String message) throws Exception{
		switch(action.getActionType()){
		case DEF_ACTIVE_FORM:
		case DEF_COLUMNS_FOR_TT:
		case DEF_DELETE:
		case DEF_FORM_DEF:
		case DEF_FORM_VERS:
		case DEF_FORMS:
		case DEF_SAVE:
		case DEF_TARGET_TABLE:
		case DEF_UPDATE:
			
			return sendMessage(IFormComponent.SERVLET_FORM_DEFINITION, message);
		case INST_DELETE:
		case INST_FIELD_STATE:
		case INST_GETTOSUBMIT:
		case INST_GETTOVALIDATE:
		case INST_RUNNING:
		case INST_SAVE:
		case INST_UPDATE:
			return sendMessage(IFormComponent.SERVLET_FORM_INSTANCE, message);
			
		case LAU_INSTANCIATE:
			return sendMessage(IFormComponent.SERVLET_FORM_LAUNCHER, message);
		}
		
		throw new Exception("XMLActionType " + action.getActionType().name() + " not supported") ;
	}
	
	
	private String sendMessage(String servlet, String message) throws Exception{
		URL url = new URL(this.url + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
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
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			
			result = IOUtils.toString(sock.getErrorStream(), "UTF-8"); 
			throw new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
		}
		
		
		
		
	}


	public void setUrl(String vanillaRuntimeUrl) {
		if (vanillaRuntimeUrl.endsWith("/")) {
			vanillaRuntimeUrl = vanillaRuntimeUrl.substring(0, vanillaRuntimeUrl.length() - 1);
		}
		
		this.url = vanillaRuntimeUrl;
		
	}
}
