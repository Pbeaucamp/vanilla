package bpm.gwt.workflow.commons.client;

import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IWorkflowAppManager {
	
	public ImageResource getImage(TypeActivity type, boolean black);
	public String getLabel(TypeActivity type);
	
	public InfoUser getInfoUser();

	public Activity buildActivity(TypeActivity type, String name);
	public PropertiesPanel<Activity> buildActivityProperties(WorkspacePanel creationPanel, BoxItem boxItem, TypeActivity type, Activity activity);

	public List<StackNavigationPanel> getCategories(CollapsePanel collapsePanel);
	
	public void manageWorkflow(Workflow workflow, boolean modify, AsyncCallback<Workflow> callback);
	public void reloadConsult();
	
	public IResourceManager getResourceManager();
}
