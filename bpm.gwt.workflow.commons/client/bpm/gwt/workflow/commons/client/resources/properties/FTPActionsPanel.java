package bpm.gwt.workflow.commons.client.resources.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.attributes.FTPAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FTPActionsPanel extends Composite {

	private static FTPActionsPanelUiBinder uiBinder = GWT.create(FTPActionsPanelUiBinder.class);

	interface FTPActionsPanelUiBinder extends UiBinder<Widget, FTPActionsPanel> {
	}

	@UiField
	HTMLPanel mainPanel;

	private IResourceManager resourceManager;
	private List<WidgetAction> widgetActions;

	public FTPActionsPanel(IResourceManager resourceManager, Cible cible) {
		initWidget(uiBinder.createAndBindUi(this));
		this.resourceManager = resourceManager;

		if (cible.getActions() != null) {
			for (FTPAction action : cible.getActions()) {
				createAction(action);
			}
		}
	}
	
	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		createAction(null);
	}

	private void createAction(FTPAction action) {
		if (widgetActions == null) {
			widgetActions = new ArrayList<WidgetAction>();
		}
		
		WidgetAction widg = new WidgetAction(this, resourceManager, action);
		mainPanel.add(widg);
		
		widgetActions.add(widg);
	}
	
	public void removeAction(WidgetAction widg) {
		widgetActions.remove(widg);
		
		widg.removeFromParent();
	}

	public List<FTPAction> getActions() {
		List<FTPAction> actions = new ArrayList<FTPAction>();
		if (widgetActions != null) {
			for (WidgetAction widg : widgetActions) {
				actions.add(widg.getAction());
			}
		}
		return actions;
	}
}
