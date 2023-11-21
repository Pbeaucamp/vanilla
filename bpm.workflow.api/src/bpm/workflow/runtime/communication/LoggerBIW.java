package bpm.workflow.runtime.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Tool for the communication with the logging Servlet of the Workflow (WorkflowLogServlet)
 * @author Charles MARTIN
 *
 */
public class LoggerBIW {
	private String vanillaurl;
	
	/**
	 * Create a logger of Metrics 
	 * @param vanilla url
	 */
	public LoggerBIW(String vanillaurl) {
		this.vanillaurl = vanillaurl;
	}
	
	/**
	 * Add a log into the log table (biw_log)
	 * @param d : the log to add
	 * @return
	 * @throws Exception
	 */
	public int addLog(LogBIW d) throws Exception {		
		String toSend = "<add>\n";

		toSend += d.getXml();
		
		toSend += "</add>";
		
		String result = sendMessage(toSend);
		
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		
		return Integer.valueOf(result);
	}
	

	/**
	 * read the XML
	 * @param string Input
	 * @return
	 * @throws IOException
	 */
	private String readXML(InputStream strIn) throws IOException {
        BufferedReader s = new BufferedReader(new InputStreamReader(strIn, "UTF-8"));
        String line = s.readLine();
        String doc="";

        while (line != null) {
            doc+=line +"\n";
            line = s.readLine();
        }
        s.close();
        
        return doc;
	}
	

	
	
	/**
	 * Get the average time of execution for a process
	 * @param id of the directory item
	 * @return the average time
	 * @throws Exception
	 */
	public String getTimeExec(int id) throws Exception {
		
		String toSend = "<gettimeexec>\n";

		toSend += "<id>";
		toSend += id;
		toSend +="</id>";
		
		toSend += "</gettimeexec>";
		
		String result = sendMessage(toSend);

		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		else {
			Document document;
			try {
				document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				return root.getStringValue();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
	
			return null;
		}
		
	}
	
	/**
	 * Get the execution time of the process for a precise date
	 * @param id of the directory item
	 * @param Dateprocess : the date to look for
	 * @return the execution time of the process for this precise date
	 * @throws Exception
	 */
	public String getTimeProcessDate(int id,String Dateprocess) throws Exception {
		
		String toSend = "<getprocessdate>\n";

		toSend += "<id>";
		toSend += id;
		toSend +="</id>";
		
		toSend += "<date>";
		toSend += Dateprocess;
		toSend +="</date>";
		
		toSend += "</getprocessdate>";
		
		String result = sendMessage(toSend);
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		else {
			Document document;
			try {
				document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				return root.getStringValue();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
	
			return null;
		}
		
	}
	
	/**
	 * Get the time of execution of the precise activity for the precise date
	 * @param id of the directory item
	 * @param name of the activity
	 * @param Dateprocess : the date to look for
	 * @return the execution time of the activity for this precise date
	 * @throws Exception
	 */
	public String getTimeActivityDate(int id,String nameact,String Dateprocess) throws Exception {
		
		String toSend = "<getactdate>\n";

		toSend += "<id>";
		toSend += id;
		toSend +="</id>";
		
		toSend += "<name>";
		toSend += nameact;
		toSend +="</name>";
		
		toSend += "<date>";
		toSend += Dateprocess;
		toSend +="</date>";
		
		toSend += "</getactdate>";
		
		String result = sendMessage(toSend);
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		else {
			Document document;
			try {
				document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				return root.getStringValue();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
	
			return null;
		}
		
	}

	/**
	 * Get the average times of execution of the activities for a process
	 * @param id of the directory item	
	 * @return the map containing <the name of the activity, <the name, the value>>
	 * @throws Exception
	 */
	public HashMap<String,List<String>> getTimeperAct(int id) throws Exception {
		
		String toSend = "<gettimeperact>\n";

		toSend += "<id>";
		toSend += id;
		toSend +="</id>";
		
		toSend += "</gettimeperact>";
		
		String result = sendMessage(toSend);
		
		HashMap<String,List<String>> retour = new HashMap<String,List<String>>();
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		else {
			Document document;
			try {
				document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				List<Element> maliste = root.elements("activity");
				for(Element el : maliste){
					List<String> valeurs = new ArrayList<String>();
					String nameofactivity = el.element("name").getStringValue();
					String time = el.element("time").getStringValue();
					valeurs.add(time);
					
					retour.put(nameofactivity, valeurs);
				}
				return retour;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
	
			return null;
		}
		
	}
	
	/**
	 * Get all the dates of execution of a process
	 * @param id of the directory item
	 * @return the list of the dates
	 * @throws Exception
	 */
public List<String> getDates(int id) throws Exception {
		
		String toSend = "<getdates>\n";

		toSend += "<id>";
		toSend += id;
		toSend +="</id>";
		
		toSend += "</getdates>";
		
		String result = sendMessage(toSend);
		
		List<String> retour = new ArrayList<String>();
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		else {
			Document document;
			try {
				document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				List<Element> maliste = root.elements("date");
				
				for(Element el : maliste){
					retour.add(el.getStringValue());
				}
				return retour;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
	
			return null;
		}
		
	}
	
	/**
	 * Send to the servlet
	 * @param xmlMessage
	 * @return
	 * @throws Exception
	 */
	private String sendMessage(String xmlMessage) throws Exception{
//		XXX
		URL url = new URL(vanillaurl + "/WorkflowLogServlet");
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");

			
		//send datas
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));

		String toSend = "<root>\n";
		toSend += xmlMessage;
		toSend += "</root>";
		
		pw.write(toSend);
		pw.close();
		
		InputStream is = sock.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		//
		sock.disconnect();
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		
		return result;

	}


}
