package bpm.birt.fusionmaps.ui.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class ConnectionHelper {
	
	public static String sendMessage(String msg, String urltoconnect) throws Exception, IOException{
		URL url = new URL(urltoconnect);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
			
		//send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		StringBuffer toSend = new StringBuffer();
		toSend.append(msg);
		
		pw.write(toSend.toString());
		pw.close();
		
		InputStream is = sock.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		
		
		return result;
	}
	
	public static String sendMap(File file, String urltoconnect) throws Exception, IOException{

		URL urlservlet = new URL(urltoconnect);

		byte[] fileBArray = new byte[(int)file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(fileBArray); 
		in.close();

		HttpURLConnection connImage = (HttpURLConnection)urlservlet.openConnection();
		connImage.setDoInput(true);
		connImage.setDoOutput(true);
		connImage.setUseCaches(false);
		connImage.setRequestProperty("Content-Type","application/octet-stream");
		connImage.setRequestProperty ("name", file.getName());
		DataOutputStream out = new DataOutputStream(connImage.getOutputStream());
		out.write(fileBArray, 0, fileBArray.length);
		out.flush(); 
		out.close();
		
		InputStream is = connImage.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		
		
		return result;
	}

}
