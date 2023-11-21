package bpm.gwt.commons.server.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommunicatorHelper {
	
	public static String sendPostMessage(String urlStr, String servlet, String message) throws Exception{
		URL url = urlStr.endsWith("/") ? new URL(urlStr.substring(0, urlStr.length() -1 ) +  servlet) : new URL(urlStr  + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		
		sock.setRequestProperty("Content-type", "application/json;charset=UTF-8");
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
			
		//send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		
		pw.write(message);
		pw.close();
		String result = null;
		try{
			InputStream is = sock.getInputStream();
			
			BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
			result = total.toString();
			is.close();
			sock.disconnect();
			
			return result;
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public static String sendGetMessage(String urlStr, String servlet) throws Exception {
		URL url = urlStr.endsWith("/") ? new URL(urlStr.substring(0, urlStr.length() - 1) +  servlet) : new URL(urlStr  + servlet);
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("GET");
		
		String result = null;
		try{
			InputStream is = sock.getInputStream();
			
			BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
			result = total.toString();
			is.close();
			sock.disconnect();
			
			return result;
		}catch(Exception ex){
			throw ex;
		}
	}
}
