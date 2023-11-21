package bpm.forms.remote.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IValidator;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RemoteValidator implements IValidator{

	public RemoteValidator(){
		
	}
	
		
	
	

	private String generateUrl(IFormInstance instance, String servletName, IRepositoryContext repositoryContext){
		StringBuffer buf = new StringBuffer();
		
		buf.append(repositoryContext.getVanillaContext().getVanillaUrl());
		buf.append("/");
		buf.append(servletName);
		buf.append("?");
		
		buf.append(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		buf.append("=");
		buf.append(instance.getId());
		
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
		buf.append("=");
		buf.append(repositoryContext.getGroup().getId());
		
		
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
		buf.append("=");
		buf.append(repositoryContext.getVanillaContext().getLogin());
		
		
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
		buf.append("=");
		buf.append(repositoryContext.getVanillaContext().getPassword());
		
		return buf.toString();
	}
	
	
	@Override

	public void invalidate(IFormInstance formInstance, Properties properties, IRepositoryContext vanillaContext) throws Exception {
				
		String baseUrl = generateUrl(formInstance, "invalidateForm", vanillaContext); 
		
		StringBuffer buf = new StringBuffer(baseUrl);
		
		for(Object o : properties.keySet()){
			buf.append("&");
			buf.append(o.toString());
			buf.append("=");
			buf.append(properties.getProperty(o.toString()));
		}
		
		sendMessage(buf.toString());
		
	}

	@Override
	public void validate(IFormInstance formInstance, Properties fieldValues, IRepositoryContext profil) throws Exception {
	
		String baseUrl = generateUrl(formInstance, "validateForm", profil); 
		sendMessage(baseUrl);
		
	}
	
	private void sendMessage(String urlWithParam) throws Exception{
		URL url = new URL(urlWithParam );
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
			
		//send datas
		
		String result = null;
		try{
			InputStream is = sock.getInputStream();
		
			result = IOUtils.toString(is, "UTF-8");
			is.close();
			
			//error catching
			if (result.contains("<error>")){
				throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			
			result = IOUtils.toString(sock.getErrorStream(), "UTF-8"); 
			throw new Exception( result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>")));
		}
	}
}
