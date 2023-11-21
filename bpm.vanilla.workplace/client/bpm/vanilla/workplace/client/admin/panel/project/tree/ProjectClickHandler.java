package bpm.vanilla.workplace.client.admin.panel.project.tree;

import bpm.vanilla.workplace.client.admin.panel.project.PanelProject;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ProjectClickHandler implements ClickHandler {

	private PanelProject panelParent;
	private PlaceWebProject project;
	
	public ProjectClickHandler(PanelProject panelParent, PlaceWebProject project) {
		this.panelParent = panelParent;
		this.project = project;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		panelParent.setCurrentProject(project);
	}
}
