package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BIWObject;
import bpm.workflow.runtime.resources.BiRepositoryObject;

/**
 * Launch a BiWorkflow already deployed
 * @author Charles MARTIN
 *
 */
public class BiWorkFlowActivity extends AbstractActivity implements IRepositoryItem, IParameters,IComment{

	protected BIWObject biObject;
	private String idBIW = "";
	private String comment;
	private static int number = 0;
	
	private HashMap<String, String> mapParameters = new HashMap<String, String>();
	
	/**
	 * do not use, only existing for parsing XML
	 */
	public BiWorkFlowActivity(){
		number++;
	}
	/**
	 * Create a BIW activity with the specified name
	 * @param name
	 */
	public BiWorkFlowActivity(String name){	
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	/**
	 * 
	 * @return name of the BIW
	 */
	public String getIdBIW() {
		return idBIW;
	}

	/**
	 * Set the name of the BIW
	 * @param idBIW
	 */
	public void setIdBIW(String idBIW) {
		this.idBIW = idBIW;
	}

	public IActivity copy() {
		BiWorkFlowActivity a = new BiWorkFlowActivity();
		a.setName("copy of " + name);
		if (biObject != null)
			a.setBIWObject(biObject);
		
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);	
			
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

	
	/**
	 * 
	 * @return the BIWObject : the BIW to launch
	 */
	public BIWObject getBiObject(){
		return biObject;
	}
	
	/**
	 * Set the BIWObject: the BIW to launch
	 * @param biObject
	 */
	public void setBIWObject(BIWObject biObject) {
		this.biObject = biObject;
		
	}
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("BIWActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if(!this.getIdBIW().equalsIgnoreCase("")){
		e.addElement("idBIW").setText(this.getIdBIW());
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

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
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

	public void decreaseNumber() {
		number--;
	}
	@Override
	public BiRepositoryObject getItem() {
		return biObject;
	}
	@Override
	public void setItem(BiRepositoryObject obj) {
		if(obj instanceof BIWObject) {
			this.biObject = (BIWObject) obj;
		}
	}
	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
		
			HashMap<String, List<String>> parameterValues = new HashMap<String, List<String>>();
			for(String s : mapParameters.keySet()) {
				String pName = s;
				List<String> pValues = workflowInstance.getListVariable().get(mapParameters.get(s)).getValues();
				
				parameterValues.put(pName, pValues);
			}
	
			List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
			int i = 1;
	
			for(String k : parameterValues.keySet()) {
				VanillaGroupParameter g = new VanillaGroupParameter();
				g.setName("group " + i);
				i++;
				g.addParameter(new VanillaParameter(k, parameterValues.get(k).get(0)));
				params.add(g);
			}
			RemoteWorkflowComponent remote = new RemoteWorkflowComponent(workflowInstance.getRepositoryApi().getContext().getVanillaContext());
			RuntimeConfiguration conf = new RuntimeConfiguration(workflowInstance.getRepositoryApi().getContext().getGroup().getId(), new ObjectIdentifier(workflowInstance.getRepositoryApi().getContext().getRepository().getId(),biObject.getId()), params);
	
			try {
				remote.startWorkflow(conf);
				activityResult = true;
				
				super.finishActivity();
			} catch(Exception e) {
				e.printStackTrace();
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				
				super.finishActivity();
			}
		}
	}
	
}
