package bpm.vanilla.platform.core.beans.tasks;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.utils.IOWriter;

public class TaskListParser {

	public static final String REP_ID = "repositoryId";
	public static final String REP_ENCRYPTED = "encrypted";
	public static final String DIR_IT_ID = "directoryItemId";

	public final static String TASK_PRIORITY = "taskPriority";
	public final static String ALTERNATE_CONNECTIONS = "alternateConnctions";
	public final static String OUTPUTFORMAT = "outputFormat";
	
	
	
	public static TaskList parse(InputStream is) throws Exception{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOWriter.write(is, os, true, true);
		return parse(os.toString());
	}
	public static TaskList parse(String is) throws Exception{
		Document doc = DocumentHelper.parseText(is);
		Element root = doc.getRootElement();
		
		TaskList l = new TaskList(ServerType.getByName(root.attributeValue("targetServerType")));
		l.setName(root.attributeValue("name"));
		l.setRepositoryId(root.attributeValue("repositoryId"));
		
		for(Element e : (List<Element>)root.elements("group")){
			l.addGroup(e.getStringValue());
		}
		for(Element e : (List<Element>)root.elements("task")){
//			String tName =e.attribute("name") != null ? e.attributeValue("name") : "";
//			
//			int type = Integer.parseInt(e.attributeValue("type"));
//			int subtype = Integer.parseInt(e.attributeValue("subtype"));
			
//			TaskDatas td = new TaskDatas(tName, "", l,props, params, type, subtype);
			IRuntimeConfig td = null;
			
			if(l.getType() == ServerType.GATEWAY) {
				td = new GatewayRuntimeConfiguration();
			}
			
			else if(l.getType() == ServerType.REPORTING) {
				td = new ReportRuntimeConfig();
			}
			
			setPropertys(td, (List<Element>)e.elements("property"));
			
			List<VanillaGroupParameter> pas = new ArrayList<VanillaGroupParameter>();
			for(Element p : (List<Element>)e.elements("parameter")){
				VanillaGroupParameter pa = new VanillaGroupParameter();
				pa.addParameter(new VanillaParameter(p.attributeValue("name"), p.getStringValue()));
				
			}
			
			if(l.getType() == ServerType.GATEWAY) {
				((GatewayRuntimeConfiguration)td).setParameters(pas);
			}
			
			else if(l.getType() == ServerType.REPORTING) {
				((ReportRuntimeConfig)td).setParameters(pas);
			}
			
			l.addTask(td);
		}
		
		return l;
		
	}


	private static void setPropertys(IRuntimeConfig td, List<Element> elements) {
		if(td instanceof GatewayRuntimeConfiguration) {
			setGatewayPropertys((GatewayRuntimeConfiguration)td, elements);
		}
		else if(td instanceof ReportRuntimeConfig) {
			setReportPropertys((ReportRuntimeConfig)td, elements);
		}
	}


	private static void setReportPropertys(ReportRuntimeConfig td, List<Element> elements) {
		int repId = -1;
		int dirItId = -1;
		String altDs = null;
		String outForm = null;
		
		for(Element p : elements){
			
			String name = p.attributeValue("name");
			String value = p.getStringValue();
			
			if(name != null && name.equals(REP_ID)) {
				repId = Integer.parseInt(value);
			}
			else if(name != null && name.equals(DIR_IT_ID)) {
				dirItId = Integer.parseInt(value);
			}
			else if(name != null && name.equals(ALTERNATE_CONNECTIONS)) {
				altDs += value;
			}
			else if(name != null && name.equals(OUTPUTFORMAT)) {
				outForm = value;
			}
		}
		
		ObjectIdentifier id = new ObjectIdentifier(repId, dirItId);
		td.setObjectIdentifier(id);
		
		td.setOutputFormat(outForm);
		
		if(altDs != null) {
			String[] dssss = altDs.split(";");
			for(String altDss : dssss) {
			
				AlternateDataSourceConfiguration alt = new AlternateDataSourceConfiguration();
				String[] altParts = altDss.split("/");
				
				alt.setConnection(altParts[0], altParts[1].substring(0, altParts[1].length() - 1));
				td.setAlternateDataSourceConfiguration(alt);
			}
		}
		
	}


	private static void setGatewayPropertys(GatewayRuntimeConfiguration td, List<Element> elements) {
		
		int repId = -1;
		int dirItId = -1;
		String altDs = null;
		
		for(Element p : elements){
			
			String name = p.attributeValue("name");
			String value = p.getStringValue();
			
			if(name != null && name.equals(REP_ID)) {
				repId = Integer.parseInt(value);
			}
			else if(name != null && name.equals(DIR_IT_ID)) {
				dirItId = Integer.parseInt(value);
			}
			else if(name != null && name.equals(ALTERNATE_CONNECTIONS)) {
				altDs += value;
			}
		}
		
		ObjectIdentifier id = new ObjectIdentifier(repId, dirItId);
		td.setObjectIdentifier(id);
		
		
		if(altDs != null) {
			String[] dssss = altDs.split(";");
			for(String altDss : dssss) {
			
				AlternateDataSourceConfiguration alt = new AlternateDataSourceConfiguration();
				String[] altParts = altDss.split("/");
				
				try {
					alt.setConnection(altParts[0], altParts[1].substring(0, altParts[1].length() - 1));
				} catch(Exception e) {

				}
				td.setAlternateDataSourceConfiguration(alt);
			}
		}
	}
}

