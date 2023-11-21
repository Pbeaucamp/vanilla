package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.workflow.activity.AirScriptActivity;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.services.SmartAirService;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AirScriptActivityProperties extends PropertiesPanel<Activity> {

	private AirScriptActivity activity;
	
	private PropertiesListBox lstProjects;
	private PropertiesListBox lstScripts;
	
	private List<AirProject> airProjects;
	
	private ChangeHandler changeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			if(event.getSource() == lstProjects.getListBox()) {
				loadScriptList();
			}
		}
	};

	public AirScriptActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, AirScriptActivity airActivity, int userId) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, airActivity.getName(), false, false);
		
		this.activity = airActivity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		lstProjects = addList(LabelsConstants.lblCnst.AirProject(), new ArrayList<ListItem>(), WidgetWidth.PCT, changeHandler, null);
		
		lstScripts = addList(LabelsConstants.lblCnst.AirScript(), new ArrayList<ListItem>(), WidgetWidth.PCT, changeHandler, null);
		
		SmartAirService.Connect.getInstance().getProjects(userId, new AsyncCallback<List<AirProject>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}

			@Override
			public void onSuccess(List<AirProject> result) {
				airProjects = result;
				List<ListItem> lstItems = new ArrayList<ListItem>();
				int i = 0;
				int selectedIndex = -1;
				for(AirProject proj : airProjects) {
					if(activity.getrProjectId() > 0 && proj.getId() == activity.getrProjectId()) {
						selectedIndex = i;
					}
					ListItem lstItem = new ListItem(proj.getName(), proj.getId());
					lstItems.add(lstItem);
					i++;
				}
				lstProjects.setItems(lstItems);
				if(selectedIndex > -1) {
					lstProjects.setSelectedIndex(selectedIndex);
					loadScriptList();
				}
			}
		});
		
	}
	
	public void loadScriptList() {
		String value = lstProjects.getValue(lstProjects.getSelectedIndex());
		int id = Integer.parseInt(value);
		for(AirProject p : airProjects) {
			if(p.getId() == id) {
				List<ListItem> lstItems = new ArrayList<ListItem>();
				int i = 0;
				int selectedIndex = -1;
				for(RScript sc : p.getScripts()) {
					if(activity.getrScriptId() > 0 && sc.getId() == activity.getrScriptId()) {
						selectedIndex = i;
					}
					ListItem lstItem = new ListItem(sc.getName(), sc.getId());
					lstItems.add(lstItem);
					i++;
				}
				lstScripts.setItems(lstItems);
				if(selectedIndex > -1) {
					lstScripts.setSelectedIndex(selectedIndex);
				}
			}
		}
	}

	@Override
	public Activity buildItem() {
		String value = lstProjects.getValue(lstProjects.getSelectedIndex());
		int id = Integer.parseInt(value);
		activity.setrProjectId(id);
		String valueS = lstScripts.getValue(lstScripts.getSelectedIndex());
		int idS = Integer.parseInt(valueS);
		activity.setrScriptId(idS);
		return activity;
	}

	@Override
	public boolean isValid() {
		return lstScripts.getSelectedIndex() > -1;
	}

}
