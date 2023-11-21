package bpm.fd.runtime.engine.datas.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class OlapViewHelper {
	public static String GROUP = "_group";
	public static String VIEWID = "_viewId";
	public static String REPID = "_repId";
	public static String WIDTH = "_width";
	
	public static String generateHTML(String login, String password, int groupId, int repositoryId, int width, String componentId, int viewId, String options, String params) throws Exception{
				
		StringBuilder b = new StringBuilder();
		b.append(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
		b.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		b.append("?");
		b.append(GROUP);b.append("=");b.append(groupId);
		b.append("&");b.append(VIEWID);b.append("=");b.append(viewId);
		b.append("&");b.append(REPID);b.append("=");b.append(repositoryId);
		b.append("&");b.append(WIDTH);b.append("=");b.append(width);
		
		if (params != null){
			b.append(params.replace(" ", "+"));
		}
		URL url = new URL(b.toString());
		
		HttpURLConnection sock = (HttpURLConnection)url.openConnection();
		sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_UNITEDOLAP);
		String authentication = new String(Base64.encodeBase64(new String(login + ":" + password).getBytes()));
		sock.setRequestProperty( "Authorization", "Basic " + authentication );
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		InputStream input = sock.getInputStream();
		
		
		ByteArrayOutputStream output1 = new ByteArrayOutputStream();
		
		//copy input to output1
		int sz = 0;
		byte[] buf = new byte[1024];
		while( (sz = input.read(buf)) >= 0){
			output1.write(buf, 0, sz);
		}
		
		sock.disconnect();
//		Document doc = null;
//		try{
//			doc = DocumentHelper.parseText(new String(output1.toByteArray(), "UTF-8"));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw ex;
//		}
//		Element body = doc.getRootElement().element("body");
		
		
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		XMLWriter writer = new XMLWriter(os, OutputFormat.createPrettyPrint());
//		writer.write("<div id='"+ componentId+ "' " + options + ">\n");
//		writer.write(output1.toByteArray());
//		writer.write("</div>\n");
		
		return output1.toString();
		
	}
}
