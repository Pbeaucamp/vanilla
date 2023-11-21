package bpm.profiling.runtime.helper;

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

	
	public static List<String> getGroups(String schedulerServerUrl) throws Exception{
		
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
			throw new Exception("Error when contacting the server\n" + e2.getMessage(), e2);
		}

		
		try {
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("GET");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
			throw new Exception("Error when dialoging with the server\n" + e1.getMessage(), e1);
		}
		sock.setRequestProperty("Content-type", "text/xml");
		
		try {
			sock.connect();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("Error when connecting to the server\n" + e1.getMessage(), e1);
		}
		
		InputStream is = null;
		try {
			is = sock.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("Error when communicating to the server\n" + e1.getMessage(), e1);
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
			throw new Exception("Error when parsing server response\n" + e1.getMessage(), e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("Error when getting server response\n" + e1.getMessage(), e1);
		}
		return  groupNames;
	}
}
