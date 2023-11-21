package bpm.vanilla.portal.server.apiImpl;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class OrbImpl {

	public String runXform(RepositoryItem item, IRepositoryApi sock, String dirToWrite, String activityId, String processId, String urlContext) {
		String xml;
		PrintWriter writer = null;
		String name = null;
		try {
			xml = sock.getRepositoryService().loadModel(item);
			
			if (activityId != null) {
				Document doc;
				try {
					doc = DocumentHelper.parseText(xml);
					Element root = doc.getRootElement();
					
					Element model = root.element("head").element("model");
					Element instance = model.addElement("xforms:instance");
					instance.addAttribute("id", "parameters");
					Element parameters = instance.addElement("parameters");
					parameters.addElement("UUID").setText(activityId);
					parameters.addElement("ProcessId").setText(processId);
					
					String posturl = "";
					for (Parameter p : sock.getRepositoryService().getParameters(item)) {
						parameters.addElement(p.getName());
						posturl += "&" + p.getName() +"={instance('parameters')/" + p.getName() + "}";
					}
					
					Element submission = model.addElement("xforms:submission");
					submission.addAttribute("id", "send");
					submission.addAttribute("serialization", "none");
					submission.addAttribute("method", "get");
					String url = urlContext + "/ValidateActivity?UUID={instance('parameters')/UUID}&ProcessId={instance('parameters')/ProcessId}";
					submission.addAttribute("action",url + posturl);
					
					xml = doc.asXML();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				
			}
			else {

				Document doc;
				try {
					doc = DocumentHelper.parseText(xml);
					Element root = doc.getRootElement();
					
					Element model = root.element("head").element("model");
					Element instance = model.addElement("xforms:instance");
					instance.addAttribute("id", "parameters");
					Element parameters = instance.addElement("parameters");
					
					for (Parameter p : sock.getRepositoryService().getParameters(item)) {
						parameters.addElement(p.getName());
					}
					
					xml = doc.asXML();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				
			
			}
			name = item.getItemName() + new Object().hashCode();
			String dir = dirToWrite + File.separator + name + ".jsp";			
			writer = new PrintWriter(dir);
		    writer.write(xml,0,xml.length());
		     
		}catch(IOException ex){
	         ex.printStackTrace();
	    } catch (Exception e) {
			e.printStackTrace();
		}finally{
	    	if(writer != null){
	    		writer.close();
	    	}
	        
	    }
		
		return name + ".jsp";

	}
}
