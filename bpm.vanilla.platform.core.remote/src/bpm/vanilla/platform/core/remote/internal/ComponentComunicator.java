package bpm.vanilla.platform.core.remote.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;

/**
 * a base class to communicate with the Vanilla Dispatcher servlet
 * 
 * When sending a message all the authentication stuff is done(sending login/password or sessionId)
 * It had the additional HttpHeader IVanillaComponentIdentifier.P_COMPONENT_NATURE 
 * with the value given to the constructor to allow the VanillaDispatcher to find a target to forward the request
 * 
 * 
 * 
 * @author ludo
 *
 */
public class ComponentComunicator extends AbstractRemoteAuthentifier{
	private String componentNature;
	public ComponentComunicator(String componentNature){
		this.componentNature = componentNature;
	}
	
	
	
	public void init(String vanillaUrl, String login, String password){
		super.init(login, password);
	}
	
	public String sendMessage(String fullurl, String headerComponentNature, String headerActionValue) throws Exception{
		URL url = new URL(fullurl);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		sock.setRequestProperty(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION, headerActionValue);
		sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, headerComponentNature);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
			
		String result = null;
		try{
			InputStream is = sock.getInputStream();
		
			result = IOUtils.toString(is, "UTF-8");
			is.close();
			sock.disconnect();
			
			extractSessionId(sock);
			//error catching
			if (result.contains("<error>")){
				
				if (result.contains("<error><session>")){
					throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else{
					throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}
				
				
			}
			return result;
		}catch(Exception ex){
			String msg = "Failed to contact component at " + fullurl + ", reason : " + ex.getMessage();
			//ex.printStackTrace();
			//if (result)
			//new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
			throw new Exception(msg, ex);
			//throw ex;
		}
	}


	
	
	public InputStream sendMessage(String fullurl, String headerComponentNature, String headerActionValue, InputStream datas) throws Exception{
		URL url = new URL(fullurl);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		writeAuthentication(sock);
		sock.setRequestProperty(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION, headerActionValue);
		sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, headerComponentNature);
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
			
		String result = null;
		try{
			
			OutputStream os = sock.getOutputStream();
			int sz =0 ;
			byte[] buf = new byte[1024];
			while( (sz = datas.read(buf)) >= 0){
				os.write(buf, 0, sz);
			}
			os.close();
			
			
			
			
			InputStream is = sock.getInputStream();
		
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			sz =0 ;
			while( (sz = is.read(buf)) >= 0){
				bos.write(buf, 0, sz);
			}
			is.close();
			
			
			result = bos.toString();
			is.close();
			sock.disconnect();
			
			extractSessionId(sock);
			//error catching
			if (result.contains("<error>")){
				
				if (result.contains("<error><session>")){
					throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
				}
				else{
					throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
				}
				
				
			}
			return new ByteArrayInputStream(bos.toByteArray());
		}catch(Exception ex){
			ex.printStackTrace();
			new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
			throw ex;
		}
	}
	

	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
		if (componentNature != null){
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, componentNature);
		}
		

		
	}
	
}
