package bpm.vanilla.platform.core.components.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;

public class AbstractVanillaDispatcher {

	/**
	 * for fd runtime dispatcher, returns new url to client
	 * 
	 * @param req
	 * @param resp
	 * @param newurl
	 * @throws Exception
	 */
	public void sendUrlBack(HttpServletRequest req, HttpServletResponse resp, String newurl) throws Exception{
		OutputStream output = resp.getOutputStream();
		output.write(newurl.getBytes());
	}
	/**
	 * 
	 * @param req
	 * @param resp
	 * @param newurl
	 * @return a copy of the responseStream
	 * @throws Exception
	 */
	public ByteArrayOutputStream sendCopy(HttpServletRequest req, HttpServletResponse resp, String newurl) throws Exception{
		/*
		 * create a Connection
		 */
		URL url =new URL(newurl);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();

		//copy the headers
		Enumeration<String> headers = req.getHeaderNames();
		while(headers.hasMoreElements()){
			String name = headers.nextElement();
			if (req.getHeader(name) != null){
				sock.setRequestProperty(name, req.getHeader(name));
			}
		}
		Enumeration<String> attributes = req.getAttributeNames();
		while(attributes.hasMoreElements()){
			String name = attributes.nextElement();
			if (req.getAttribute(name) != null){
				sock.setRequestProperty(name, req.getAttribute(name).toString());
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
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		sz = 0;
		while( (sz = input2.read(buf)) >= 0){
			output2.write(buf, 0, sz);
			out2.write(buf, 0, sz);
		}
		
		
		//TODO : close streams???
		sock.disconnect();
		return out2;
	}
	
	
	
	
}
