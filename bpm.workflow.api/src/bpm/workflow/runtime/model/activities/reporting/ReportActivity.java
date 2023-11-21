package bpm.workflow.runtime.model.activities.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.model.IReport;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.ReportObject;
import bpm.workflow.runtime.resources.variables.ListVariable;

/**
 * Create a report 
 * @author CHARBONNIER, MARTIN
 *
 */
public class ReportActivity extends AbstractActivity implements IRepositoryItem, IOutputProvider, IComment, IReport, IParameters {
	private String comment;
	protected ReportObject biObject;
	private boolean addToGed = false;
	protected List<String> groups = new ArrayList<String>();
	private static int number = 0;
	private String generatedFilePath;
	private String pathToStore = ListVariable.GENERATED_REPORTS_HOME;
	private HashMap<String, String> mapParameters = new HashMap<String, String>();
	private boolean isMassReporting;
	
	/**
	 * do not use, only existing for parsing XML
	 */
	public ReportActivity(){
		number++;
	}
	
	/**
	 * Create a report activity with the specified name
	 * @param name
	 */
	public ReportActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		generatedFilePath = this.getId() + "_outputpath";
		number++;
	}
	
	
	/**
	 * do not use, only existing for parsing XML
	 */
	public void setReportObject(ReportObject biObject){
		this.biObject = biObject;
	}

	
	
	@Override
	public void setName(String name) {
		super.setName(name);
		generatedFilePath = this.getId() + "_outputpath";
	}

	/**
	 * 
	 * @return the ReportObject (report item)
	 */
	public ReportObject getBiObject(){
		return biObject;
	}
	
	/**
	 * Add a mapping between the form and the report
	 * @param form
	 * @param biObject
	 */
	public void addParameterLink(String form, String biObject) {
		if (!biObject.startsWith("{$")) {
			biObject = "{$" + biObject;
		}
		if (!biObject.endsWith("}")) {
			biObject += "}";
		}
		
		mapParameters.put(form, biObject);
	}
	
	/**
	 * Remove the specified mapping between the form and the report
	 * @param form
	 * @param biObject
	 */
	public void removeParameterLink(String form, String biObject) {
		for (String k : mapParameters.keySet()) {
			if (k.equalsIgnoreCase(form) && mapParameters.get(k).equalsIgnoreCase(biObject)) {
				mapParameters.remove(k);
			}
		}
	}

	/**
	 * 
	 * @return the mappings concerning the report
	 */
	public HashMap<String, String> getMappings() {
		return mapParameters;
	}
	
	/**
	 * Set the map for the parameters
	 * @param mapParameters
	 */
	public void setMapParameters(HashMap<String, String> mapParameters) {
		this.mapParameters = mapParameters;
	}
	
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("reportActivity");
		e.addElement("pathtostore").setText(pathToStore);
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (biObject != null){
			e.add(biObject.getXmlNode());
		}
		if (!mapParameters.isEmpty()) {
			e.addElement("mapping");
			for (String key : mapParameters.keySet()) {
				if(!key.equalsIgnoreCase("") && !mapParameters.get(key).equalsIgnoreCase("")){
					Element map = e.element("mapping").addElement("map"); 
					map.addElement("key").setText(key);
					map.addElement("value").setText(mapParameters.get(key));
				}
			}
		}
		
		e.addElement("addtoged").setText(addToGed + "");
		e.addElement("massreporting").setText(isMassReporting+"");
		
		if (getSecurityProvider() != null) {
			e.addElement("securityprovider").setText(getSecurityProvider());
		}
		
		return e;
	}

	public IActivity copy() {
		ReportActivity a = new ReportActivity();
		a.setName("copy of " + name);
		if (biObject != null)
			a.setReportObject(biObject);
		
		if (getSecurityProvider() != null) {
			a.setSecurityProvider(getSecurityProvider());
		}
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		a.setAddToGed(addToGed);
			
		return a;
	}
	
	public List<Integer> getOutputFormats() {
		if (biObject != null)
			return biObject.getOutputFormats();
		else
			return new ArrayList<Integer>();
	}

	public String getOutputName() {
		if (biObject != null)
			return biObject.getOutputName();
		else
			return "";
	}
	public void setOutputName(String outputName) {
		if (biObject != null) {
			biObject.setOutputName(outputName);
		}
	}

	public void setOutputFormats(List<Integer> i) {
		biObject.setOutputFormats(i);
	}


	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (biObject == null) {
			buf.append("You have to select a Report for the activity " + name + "\n");
		}
		
		return buf.toString();
	}

	public List<String> getParameters(IRepositoryApi sock) {

		if (biObject != null) {
			try {
				
				List<String> params = biObject.getParameters(sock);
				if(isMassReporting && params != null && !params.contains("_UserId_")) {
					params.add("_UserId_");
				}
				return params;
			} catch (Exception e) {
				return new ArrayList<String>();
			}
		}
		else {
			return new ArrayList<String>();
		}
	
	}

	
	public String getItemName() {
		if (biObject != null)
			return biObject.getItemName();
		else
			return null;
	}

	public void setItemName(String name) {
		if (biObject != null)
			biObject.setItemName(name);
		
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public boolean isAddToGed() {
		return addToGed;
	}

	public void setAddToGed(boolean addToGed) {
		this.addToGed = addToGed;
	}
	public void setAddToGed(String addtoged) {
		this.addToGed = new Boolean(addtoged);
	}
	
	public void setSecurityProvider(String groupName) {
		groups = new ArrayList<String>();
		groups.add(groupName);
	}
	
	public String getSecurityProvider() {
		if (groups.isEmpty())
			return null;
		
		return groups.get(0);
	}
	
	public void delSecurityProvider() {
		groups = new ArrayList<String>();
	}

	
	public void decreaseNumber() {
		number--;
	}

	public String getPathToStore() {
		return pathToStore;
	}

	public void setPathToStore(String pathToStore) {
		this.pathToStore = pathToStore;
	}


	public String getOutputVariable() {
		return generatedFilePath;
	}
	
	@Override
	public BiRepositoryObject getItem() {
		return biObject;
	}
	@Override
	public void setItem(BiRepositoryObject obj) {
		if(obj instanceof ReportObject) {
			this.biObject = (ReportObject) obj;
		}
	}

	public void setMassReporting(boolean isMassReporting) {
		this.isMassReporting = isMassReporting;
		
		if(!isMassReporting) {
			biObject.removeParameter("_UserId_");
		}
	}

	public boolean isMassReporting() {
		return isMassReporting;
	}

	public void setMassReporting(String massReporting) {
		this.isMassReporting = Boolean.parseBoolean(massReporting);
	}

	@Override
	public void setOutputFormats(int[] selectionIndices) {
		
		List<Integer> l = new ArrayList<Integer>();
		for(int i : selectionIndices) {
			l.add(i);
		}
		
		biObject.setOutputFormats(l);
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				if(isMassReporting) {
					executeMassReporting();
				}
				else {
					executeReport();
				}
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
	
			super.finishActivity();
		}
	}

	private void executeReport() throws Exception {
		//retrive parameters
		HashMap<String, String> parameterValues = new HashMap<String, String>();
		for(String s : mapParameters.keySet()) {
			String pName = s;
//			List<String> pValues = workflowInstance.getListVariable().get(mapParameters.get(s)).getValues();
			
			String value = workflowInstance.parseString(mapParameters.get(s));
			
			
			parameterValues.put(pName, value);
		}
		
		RemoteReportRuntime remote = new RemoteReportRuntime(workflowInstance.getRepositoryApi().getContext().getVanillaContext());
		VanillaParameterComponent paramRemote = new RemoteVanillaParameterComponent(workflowInstance.getRepositoryApi().getContext().getVanillaContext());
		ReportRuntimeConfig conf = new ReportRuntimeConfig();

		conf.setObjectIdentifier(new ObjectIdentifier(workflowInstance.getRepositoryApi().getContext().getRepository().getId(), biObject.getId()));
		conf.setRuntimeUrl(workflowInstance.getRepositoryApi().getContext().getVanillaContext().getVanillaUrl());

		for(String group : groups) {
			
			group = workflowInstance.parseString(group);
			
			int groupId = workflowInstance.getVanillaApi().getVanillaSecurityManager().getGroupByName(group).getId();
			User user = workflowInstance.getVanillaApi().getVanillaSecurityManager().getUserByLogin(workflowInstance.getVanillaApi().getVanillaContext().getLogin());
			
			conf.setVanillaGroupId(groupId);

			//Try to set real parameters -> if it does not work set it as before
			try {
				List<VanillaGroupParameter> params = paramRemote.getParameters(conf);
				for (VanillaGroupParameter groupParam : params) {
					for (VanillaParameter param : groupParam.getParameters()) {
						
						for(String myParam : parameterValues.keySet()) {
							if (param.getName().equals(myParam)) {
								param.addSelectedValue(parameterValues.get(myParam));
							}
						}
					}
				}
				conf.setParameters(params);
			} catch (Exception e) {
				e.printStackTrace();

				List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
				int i = 1;
				for(String k : parameterValues.keySet()) {
					VanillaGroupParameter g = new VanillaGroupParameter();
					g.setName("group " + i);
					i++;
					g.addParameter(new VanillaParameter(k, parameterValues.get(k)));
					params.add(g);
				}
				conf.setParameters(params);
			}
		
			//execute the report for each format
			for(Integer out : biObject.getOutputFormats()) {
	
				conf.setOutputFormat(ReportObject.OUTPUT_FORMATS[out].toLowerCase());
	
				try {
					InputStream is = remote.runReport(conf, user);
	
					pathToStore = workflowInstance.parseString(pathToStore);
					
					if(!pathToStore.endsWith(File.separator) || !pathToStore.endsWith("/")) {
						pathToStore += "/";
					}
					pathToStore = pathToStore.replace(File.separator, "/");

					//We remove the hashcode for now car we cant find the report in BrowseContentActivity
//					String reportFolder = pathToStore + String.valueOf(new Object().hashCode()) + "/";
					String reportFolder = pathToStore + "/";
	
					File folderStorage = new File(reportFolder);
					if(!folderStorage.exists()) {
						Logger.getLogger(getClass()).debug("The report storage folder " + pathToStore + " does not exists.");
						if(folderStorage.mkdirs()) {
							Logger.getLogger(getClass()).debug("Report storage folder " + pathToStore + " created.");
						}
					}
	
					String reportOutputName = group + "_" + workflowInstance.parseString(biObject.getOutputName());
					
					if(reportOutputName == null || reportOutputName.equals("")) {
						reportOutputName = "Report_" + new Object().hashCode();
					}
	
					Logger.getLogger(getClass()).debug("Path to store : " + pathToStore);
	
					reportOutputName += "." + conf.getOutputFormat();
	
					FileOutputStream fos = new FileOutputStream(new File(reportFolder + reportOutputName));
	
					byte buffer[] = new byte[512 * 1024];
					int nbLecture;
	
					while((nbLecture = is.read(buffer)) != -1) {
						fos.write(buffer, 0, nbLecture);
	
					}
					is.close();
					fos.close();
					
					workflowInstance.getOrCreateVariable(getOutputVariable()).addValue(reportFolder + reportOutputName);
				}
				catch(Exception e) {
					e.printStackTrace();
					Logger.getLogger(getClass()).error("Error while executing report", e);
					throw e;
				}
			}
		}
		
		activityResult = true;
	}

	private void executeMassReporting() {
		
		
	}
	
	
}
