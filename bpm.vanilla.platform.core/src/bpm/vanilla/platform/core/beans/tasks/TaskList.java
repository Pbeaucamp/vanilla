package bpm.vanilla.platform.core.beans.tasks;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;

public class TaskList {
	private String repositoryId;
	private String name;
	
	private List<IRuntimeConfig> tasks = new ArrayList<IRuntimeConfig>();
	private List<String> groups = new ArrayList<String>();
	
	private ServerType type;
	public TaskList(ServerType type){
		this.type = type;
	}
	
	public ServerType getType(){
		return type;
	}
	public void addTask(IRuntimeConfig dt){
		tasks.add(dt);
	}
	
	public void removeTask(IRuntimeConfig t){
		tasks.remove(t);
	}
	
	public void addGroup(String group){
		groups.add(group);
	}

	/**
	 * @return the tasks
	 */
	public List<IRuntimeConfig> getTasks() {
		return tasks;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId() {
		return repositoryId;
	}

	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Element getElement(){
		Element e = DocumentHelper.createElement("taskList");
		e.addAttribute("name", getName());
		e.addAttribute("repositoryId", getRepositoryId());
		e.addAttribute("vanillaUrl", "vanillaUrl");
		e.addAttribute("targetServerType", getType().getTypeName());
		for(String s : groups){
			e.addElement("group").setText(s);
		}
		
		for(IRuntimeConfig td : tasks){
			
			Element _td = e.addElement("task");
			
//			_td.addAttribute("name", td.getName() + "");
//			_td.addAttribute("type", td.getObjectType() + "");
//			_td.addAttribute("subtype", td.getObjectSubType() + "");
			
			if(td instanceof GatewayRuntimeConfiguration) {
				createGatewayProperties(_td, ((GatewayRuntimeConfiguration)td));
			}
			else if (td instanceof  ReportRuntimeConfig) {
				
				createReportProperties(_td, ((ReportRuntimeConfig)td));
			}
			
//			Enumeration en = td.getTaskProperties().propertyNames();
//			while(en.hasMoreElements()){
//				String s = (String)en.nextElement();
//				if (td.getTaskProperties().get(s) != null){
//					_td.addElement("property").addAttribute("name", s).setText(td.getTaskProperties().getProperty(s));
//				}
//			}
//			
//			en = td.getTaskParameters().propertyNames();
//			while(en.hasMoreElements()){
//				String s = (String)en.nextElement();
//				if (td.getTaskParameters().get(s) != null){
//					_td.addElement("parameter").addAttribute("name", s).setText(td.getTaskParameters().getProperty(s));
//				}
//			}

		}
		
		return e;
	}

	
	private void createReportProperties(Element td, ReportRuntimeConfig reportRuntimeConfig) {
		td.addElement("property").addAttribute("name", TaskListParser.DIR_IT_ID).setText(reportRuntimeConfig.getObjectIdentifier().getDirectoryItemId() + "");
		td.addElement("property").addAttribute("name", TaskListParser.REP_ID).setText(reportRuntimeConfig.getObjectIdentifier().getRepositoryId() + "");
		if(reportRuntimeConfig.getAlternateConnectionsConfiguration() != null) {
			String tmpAltCons = "";
			for (String altKey : reportRuntimeConfig.getAlternateConnectionsConfiguration().getDataSourcesNames()) {
				String altVal = reportRuntimeConfig.getAlternateConnectionsConfiguration().getConnection(altKey);
				if (altVal == null){
					continue;
				}
				tmpAltCons += altKey + "/" + altVal + ";";
			}
			td.addElement("property").addAttribute("name", TaskListParser.ALTERNATE_CONNECTIONS).setText(tmpAltCons);
		}
		td.addElement("property").addAttribute("name", TaskListParser.OUTPUTFORMAT).setText(reportRuntimeConfig.getOutputFormat() + "");
	}

	private void createGatewayProperties(Element td, GatewayRuntimeConfiguration gatewayRuntimeConfiguration) {
		td.addElement("property").addAttribute("name", TaskListParser.DIR_IT_ID).setText(gatewayRuntimeConfiguration.getObjectIdentifier().getDirectoryItemId() + "");
		td.addElement("property").addAttribute("name", TaskListParser.REP_ID).setText(gatewayRuntimeConfiguration.getObjectIdentifier().getRepositoryId() + "");
		if(gatewayRuntimeConfiguration.getAlternateDataSourceConfiguration() != null) {
			String tmpAltCons = "";
			for (String altKey : gatewayRuntimeConfiguration.getAlternateDataSourceConfiguration().getDataSourcesNames()) {
				String altVal = gatewayRuntimeConfiguration.getAlternateDataSourceConfiguration().getConnection(altKey);
				if (altVal == null){
					continue;
				}
				tmpAltCons += altKey + "/" + altVal + ";";
			}
			td.addElement("property").addAttribute("name", TaskListParser.ALTERNATE_CONNECTIONS).setText(tmpAltCons);
		}
		
	}

	public String asXmlString(){
		return getElement().asXML();
	}
	
	public void saveIn(OutputStream stream) throws Exception{
		XMLWriter w = new XMLWriter(stream, OutputFormat.createPrettyPrint());
		w.write(getElement());
		w.close();
	}
}
