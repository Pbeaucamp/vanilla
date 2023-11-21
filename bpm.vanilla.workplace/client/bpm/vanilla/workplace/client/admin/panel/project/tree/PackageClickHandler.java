package bpm.vanilla.workplace.client.admin.panel.project.tree;

import bpm.vanilla.workplace.client.admin.panel.project.PanelProject;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class PackageClickHandler implements ClickHandler {

	private PanelProject panelParent;
	private PlaceWebPackage currentPackage;
	
	public PackageClickHandler(PanelProject panelParent, PlaceWebPackage currentPackage) {
		this.panelParent = panelParent;
		this.currentPackage = currentPackage;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		panelParent.setCurrentPackage(currentPackage);
	}
}
