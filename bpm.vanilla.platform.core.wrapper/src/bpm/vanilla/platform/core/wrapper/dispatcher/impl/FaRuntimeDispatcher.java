package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaDispatcher;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;

/**
 * dispatcher used to call FAView from  FD or VanillaViewer
 * @author ludo
 *
 * @deprecated : no more used, replaced by UnitedOlapDispacther
 */
public abstract class FaRuntimeDispatcher extends AbstractVanillaDispatcher implements IDispatcher{
	
	
	private FactoryDispatcher factory;
	public FaRuntimeDispatcher(FactoryDispatcher factory){

		this.factory = factory;
	}
	@Override
	public void dispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		
		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
		
		if (ids.isEmpty()){
			throw new VanillaException("No UnitedOlap registered within Vanilla");
		}
		
		StringBuilder urlStr = new StringBuilder();
		urlStr.append(ids.get(0).getComponentUrl());
		
		urlStr.append("/faRuntime/FdServlet");
		urlStr.append("?");
		Enumeration<String> en = req.getParameterNames();
		boolean firstP = true;

		while(en.hasMoreElements()){
			String key = en.nextElement();
			
			if (firstP){
				firstP = false;
			}
			else{
				urlStr.append("&");
			}
			urlStr.append(key + "=");
			if (req.getParameter(key) != null){
				urlStr.append(req.getParameter(key));
			}
			
			
		}
		
		

		/*
		 * create a Connection
		 */
		URL url =new URL(urlStr.toString());
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		if (req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID) != null){
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID));
		}
		else if (req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID) != null){
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, (String)req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID));
		}
		else{
			resp.sendError(resp.SC_UNAUTHORIZED, "Missing VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID Header");
			return;
		}
		
		//copy the headers
		Enumeration<String> headers = req.getHeaderNames();
		while(headers.hasMoreElements()){
			String name = headers.nextElement();
			if (req.getHeader(name) != null){
				sock.setRequestProperty(name, req.getHeader(name));
			}
		}
		


		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		
		
		InputStream input1 = req.getInputStream();
		OutputStream output1 = sock.getOutputStream();
		
		//copy input to output1
		int sz = 0;
		byte[] buf = new byte[1024];
		while( (sz = input1.read(buf)) >= 0){
			output1.write(buf, 0, sz);
		}
		
		
		
		InputStream input2 = sock.getInputStream();
		OutputStream output2 = resp.getOutputStream();
		
		sz = 0;
		while( (sz = input2.read(buf)) >= 0){
			output2.write(buf, 0, sz);
		}
		
		
		//TODO : close streams???
		sock.disconnect();
		
	}
	@Override
	public boolean needAuthentication() {
		return true;
	}
	
	/**
	 * write a requestProperty named VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID with the sessionId if it exists
	 * or Authaurization Header fro Basic Base64 Encoded
	 * @param sock
	 */
	protected void writeAuthentication(HttpURLConnection sock, String login, String password){
		String authentication = new String(Base64.encodeBase64(new String(login + ":" + password).getBytes()));
		sock.setRequestProperty( "Authorization", "Basic " + authentication );
	}
}
