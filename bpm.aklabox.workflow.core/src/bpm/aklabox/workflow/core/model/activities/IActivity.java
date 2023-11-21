package bpm.aklabox.workflow.core.model.activities;

import java.util.List;

import bpm.document.management.core.model.OrganigramElementSecurity.Fonction;

public interface IActivity {

	public void setActivityId(String activityId);

	public void setActivityName(String activityName);

	public void setPosX(int posX);

	public void setPosY(int posY);

	public int getPosX();

	public int getPosY();

	public String getActivityId();

	public String getActivityName();

	public void setDescription(String description);

	public String getDescription();

	public void setListIndex(int index);

	public int getListIndex();

	//public ActivityLog executeActivity();
	
	public List<String> getFormSectionIds();
	
	public String getFormSectionId();

	public void setFormSectionIds(List<String> formSectionId);
	
	public void setFormSectionId(String formSectionId);

	public String getOrgaElementId();

	public void setOrgaElementId(String orgaElementId);
	
	public List<Integer> getOrgaElementIds();
	
	public void setOrgaElementIds(List<Integer> orgaElementId);

	public String getOrgaFunction();

	public void setOrgaFunction(String orgaFunction);
	
	public List<Fonction> getOrgaFunctions();
	
	public void setOrgaFunctions(List<Fonction> orgaFunction);
}
