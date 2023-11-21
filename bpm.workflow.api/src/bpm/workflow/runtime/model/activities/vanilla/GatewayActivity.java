package bpm.workflow.runtime.model.activities.vanilla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.GatewayObject;

/**

	@author CAMUS, MARTIN
	XPDL Variables Descriptions to be Used in Hook or If Statement: 
 		- id + "_birepository_id"  : STRING    : repsitoryServerName in the model
 		- id + "_gtwserver_id"  : STRING: gatewayServerName in the model
		- id + "_preferenceServer_id 	: STRING : : preferencesServerName in the model
 		- id + "_param_" + biObject.getParameters(): STRING : parametersNames for the BIG
 		- id + "_succeed" : INTEGER : result of the execution of the Gateaway on the server
 (-1 : run failed, 0 : run not launched, 1: run succeed)		
 		
 		
 		- id + "_numberOfAttempts :INTEGER : number of attemps if their is error at runtime
 		- id + "_directoryItemId  : INTEGER : 
 */
public class GatewayActivity extends AbstractActivity implements IRepositoryItem, IComment, IParameters, IConditionnable {
	
	private GatewayObject biObject;
	private int numberOfAttempts = 1;
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	private String comment;
	private static int number = 0;
	

	private HashMap<String, String> mapParameters = new HashMap<String, String>();
	/**
	 * @return the numberOfAttempts
	 */
	public final int getNumberOfAttempts() {
		return numberOfAttempts;
	}

	/**
	 * @param numberOfAttempts the numberOfAttempts to set
	 */
	public final void setNumberOfAttempts(int numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}
	
	public final void setNumberOfAttempts(String numberOfAttempts) {
		try{
			this.numberOfAttempts = Integer.parseInt(numberOfAttempts);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}


	public GatewayActivity(){
		 varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);
		
		number++;
	}

	
	
	
	public String getSuccessVariableSuffix() {
		return "_succeed";
	}

	/**
	 * do not use, only existing for parsing XML
	 */
	public void setGatewayObject(GatewayObject biObject){
		this.biObject = biObject;
	}
	
	/**
	 * Create a gateway activity with the specified name
	 * @param name
	 */
	public GatewayActivity(String name){

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);
	

	}
	
	/**
	 * Create a gateway activity with the specified name and Gateway Object
	 * @param name
	 * @param biObject
	 */
	protected GatewayActivity(String name, GatewayObject biObject){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		
		this.biObject = biObject;

	}
	/**
	 * 
	 * @return the GatewayObject
	 */
	public GatewayObject getBiObject(){
		return biObject;
	}
	
	/**
	 * Set the GatewayObject
	 * @param biobject
	 */
	public void setBiObject(GatewayObject biobject) {
		this.biObject = biobject;
	}
	
	/**
	 * Add a mapping between a form field and a parameter
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
	 * Remove a mapping between a form field and a parameter
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
	 * @return all the mappings
	 */
	public HashMap<String, String> getMappings() {
		return mapParameters;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("gatewayActivity");
		e.addElement("numberOfAttempts").setText(numberOfAttempts + "");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		
		if (biObject != null){
			e.add(biObject.getXmlNode());
		}

		if (!mapParameters.isEmpty()) {
			e.addElement("mapping");
			for (String key : mapParameters.keySet()) {
				Element map = e.element("mapping").addElement("map"); 
				if(!key.equalsIgnoreCase("") && !mapParameters.get(key).equalsIgnoreCase("")){
				map.addElement("key").setText(key);
				map.addElement("value").setText(mapParameters.get(key));
				}
			}
		}
		return e;
	}

	public IActivity copy() {
		GatewayActivity a = new GatewayActivity();
		a.setName("copy of " + name);
		return a;
	}


	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (biObject == null) {
			buf.append("You have to select a Report for the activity " + name + "\n");
		}

		
		return buf.toString();
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

	public List<String> getParameters(IRepositoryApi sock) {
		if (biObject != null) {
			try {
				return biObject.getParameters(sock);
			} catch (Exception e) {
				return new ArrayList<String>();
			}
		}
		else {
			return new ArrayList<String>();
		}
	}



	public List<ActivityVariables> getVariables() {
		
		listeVar.add(varSucceed);
		
		return listeVar;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public BiRepositoryObject getItem() {
		return biObject;
	}

	@Override
	public void setItem(BiRepositoryObject obj) {
		if(obj instanceof GatewayObject) {
			this.biObject = (GatewayObject) obj;
		}
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			
			final Properties parameters = new Properties();

			for(String s  : mapParameters.keySet()){
				String value = "";
				String key = s;
				
				value = workflowInstance.parseString(mapParameters.get(s));
				parameters.put(key, value);
			}

			RemoteGatewayComponent 	remote = new RemoteGatewayComponent(workflowInstance.getRepositoryApi().getContext().getVanillaContext());

			List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
			int i = 1;
			for(Object o : parameters.keySet()){
				VanillaGroupParameter g = new VanillaGroupParameter();
				g.setName("group " + i);
				i++;
				g.addParameter( new VanillaParameter((String)o, parameters.getProperty((String)o)));
				params.add(g);

			}

			User user = workflowInstance.getVanillaApi().getVanillaSecurityManager().getUserByLogin((workflowInstance.getVanillaApi().getVanillaContext().getLogin()));
			
			ObjectIdentifier id = new ObjectIdentifier(workflowInstance.getRepositoryApi().getContext().getRepository().getId(), biObject.getId());
			
			GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration(id,params,workflowInstance.getRepositoryApi().getContext().getGroup().getId());
			conf.setRuntimeUrl(workflowInstance.getRepositoryApi().getContext().getVanillaContext().getVanillaUrl());
			
			try{
				Logger.getLogger(getClass()).debug(getName() + " running gateway");
				GatewayRuntimeState state = remote.runGateway(conf, user);
				try{
					if(state.getState() == bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.ActivityState.ENDED) {
						Logger.getLogger(getClass()).debug(getName() + " finished gateway");
						activityResult = true;
					}
					else {
						Logger.getLogger(getClass()).debug(getName() + " finished gateway with errors");
						activityResult = false;
						activityState = ActivityState.FAILED;
						failureCause = state.getFailureCause();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					activityResult = false;
					activityState = ActivityState.FAILED;
					failureCause = ex.getMessage();
				}
				
			}catch(Throwable ex){
				Logger.getLogger(getClass()).error("Error when executing Gateway", ex);
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = ex.getMessage();
			}
			
			Runtime r = Runtime.getRuntime();
			r.gc();
			
			super.finishActivity();
		}
	}
}
