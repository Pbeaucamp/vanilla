package bpm.gateway.core.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SchedulerHelper {

	
	public static List<String> getGroupNames(String schedulerServerUrl) throws Exception{
		List<String> groupNames = new ArrayList<String>();
		URL url = null;
		try {
			url = new URL(schedulerServerUrl + "/ListScheduledJobs");
		} catch (MalformedURLException e1) {
			
			e1.printStackTrace();
		}
		HttpURLConnection sock = null;
		try {
			sock = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			
			e2.printStackTrace();
		}

		
		try {
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("GET");
		} catch (ProtocolException e1) {
			
			e1.printStackTrace();
		}
		sock.setRequestProperty("Content-type", "text/xml");
		
		try {
			sock.connect();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		InputStream is = null;
		try {
			is = sock.getInputStream();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		
		try {
			Document doc = DocumentHelper.parseText(IOUtils.toString(is, "UTF-8"));
			sock.disconnect();
			Element root = doc.getRootElement();
			
			if (root.element("groups") != null){
				for(Object e : root.element("groups").elements("group")){
					groupNames.add(((Element)e).element("name").getStringValue());
				}
			}
			

		} catch (DocumentException e1) {
			e1.printStackTrace();
			throw new  Exception("Unable to parse server Response," + e1.getMessage() != null ? e1.getMessage() : "" , e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw e1;
		}
		
		return groupNames;
	}
}
