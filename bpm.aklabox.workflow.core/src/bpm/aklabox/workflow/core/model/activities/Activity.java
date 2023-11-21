package bpm.aklabox.workflow.core.model.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.Example;

import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.OrganigramElementSecurity.Fonction;

public class Activity implements Serializable, IActivity {

	private static final long serialVersionUID = 1L;

	protected int id;
	protected String activityId;
	protected String activityName;
	protected int workflowId;
	protected int posX;
	protected int posY;
	protected String description = "None";
	protected int listIndex;
	protected int versionNumber;
	
	protected String formSectionId;
	/**
	 * - In case of Static attribution :
	 * May contain a Integer as String which represent the {@link OrganigramElement#getId()} or
	 * a String which represent a list of ids separated by ; in case of multi-attribution
	 * Example : 5 or 5;9;15;3
	 * 
	 * - In case of dynamic attribution :
	 * pattern => {x}:{ActivityId}
	 * x: negative integer in relation with the top level of the dynamic target or zero if target is specified by orgaFunction
	 * Example  -1 for n+1 manager, -2 for n+2 manager or 0 for a specific manager of the executor
	 * ActivityId: (Optional) if present, define where is the executor which is the dynamic source. If not, the precedent activity is taken
	 * Example  Validation5
	 * 
	 * Note : can be a mix of both solutions (dynamic identification in last)
	 * {@link Example} 
	 */
	protected String orgaElementId;  
	
	/**
	 * String representing a function or a list of function separated by ; and ordering like formsectionId
	 */
	protected String orgaFunction;
	
	private List<OrganigramElement> orgaElement;

	public Activity() {
	}

	public Activity(Activity activity) {
		this.activityId = activity.getActivityId();
		this.activityName = activity.getActivityName();
		this.workflowId = activity.getWorkflowId();
		this.posX = activity.getPosX();
		this.posY = activity.getPosY();
		this.listIndex = activity.getListIndex();
	}

	public Activity(String activityId, String activityName, int workflowId, int posX, int posY) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
	
	@Override
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setListIndex(int index) {
		this.listIndex = index;
	}

	@Override
	public int getListIndex() {
		return listIndex;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public List<String> getFormSectionIds() {
		if(formSectionId == null) return new ArrayList<String>();
		return Arrays.asList(formSectionId.split(";"));
	}

	public void setFormSectionIds(List<String> formSectionId) {
		String ids = "";
		for(String id : formSectionId){
			ids = ids + id + ";";
		}
		if(!ids.isEmpty()){
			ids = ids.substring(0, ids.length()-1);
		}
		
		this.formSectionId = ids;
	}
	
	public String getFormSectionId() {
		return formSectionId;
	}

	public void setFormSectionId(String formSectionId) {
		this.formSectionId = formSectionId;
	}

	public String getOrgaElementId() {
		return orgaElementId;
	}

	public void setOrgaElementId(String orgaElementId) {
		this.orgaElementId = orgaElementId;
	}
	
	public List<Integer> getOrgaElementIds() {
		List<Integer> res = new ArrayList<Integer>();
		if(orgaElementId == null || orgaElementId.isEmpty()) return res;
		for(String id : orgaElementId.split(";")){
			if(id.contains(":")){
				res.add(Integer.parseInt(id.split(":")[0]));
			} else {
				res.add(Integer.parseInt(id));
			}
			
		}
		return res;
	}

	public void setOrgaElementIds(List<Integer> orgaElementId) {
		String ids = "";
		for(int id : orgaElementId){
			ids = ids + id + ";";
		}
		if(!ids.isEmpty()){
			ids = ids.substring(0, ids.length()-1);
		}
		this.orgaElementId = ids;
	}

	public String getOrgaFunction() {
		return orgaFunction;
	}

	public void setOrgaFunction(String orgaFunction) {
		this.orgaFunction = orgaFunction;
	}
	
	public List<Fonction> getOrgaFunctions() {
		List<Fonction> res = new ArrayList<Fonction>();
		if(orgaFunction == null || orgaFunction.isEmpty()) return res;
		for(String f : orgaFunction.split(";")){
			res.add(Fonction.values()[Integer.parseInt(f)]);
		}
		return res;
	}

	public void setOrgaFunctions(List<Fonction> orgaFunction) {
		String ids = "";
		for(Fonction f : orgaFunction){
			ids = ids + f.ordinal() + ";";
		}
		if(!ids.isEmpty()){
			ids = ids.substring(0, ids.length()-1);
		}
		this.orgaFunction = ids;
	}

	public List<OrganigramElement> getOrgaElements() {
		return orgaElement;
	}

	public void setOrgaElements(List<OrganigramElement> orgaElement) {
		this.orgaElement = orgaElement;
	}

	public void addOrgaElement(OrganigramElement orgaElement) {
		if(this.orgaElement == null) this.orgaElement = new ArrayList<>();
		this.orgaElement.add(orgaElement);
	}
}
