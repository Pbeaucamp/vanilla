package bpm.smart.web.client.panels.resources;

import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.ICatchCollapsePanel;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.tabs.HorizontalTab;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.UserSession;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.panels.LogRPanel;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class AirPanel extends HorizontalTab implements ICatchCollapsePanel {

	private static AirPanelUiBinder uiBinder = GWT.create(AirPanelUiBinder.class);

	interface AirPanelUiBinder extends UiBinder<Widget, AirPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	CollapsePanel collapsePanel;
	
	private LogRPanel logPanel;
	private NavigationPanel navPanel;
	private MainPanel mainPanel;
	private WorkspacePanel workspacePanel;
	
	private IResourceManager resourceManager;
	private UserSession userSession;

	public AirPanel(LogRPanel logPanel, MainPanel mainPanel, IResourceManager resourceManager) {
		super(LabelsConstants.lblCnst.TabWorkspace(), Images.INSTANCE.ic_web_black_18dp());
		collapsePanel = new CollapsePanel(450, 46);
		add(uiBinder.createAndBindUi(this));
		addStyleName(style.mainPanel());
		this.logPanel = logPanel;
		this.userSession = mainPanel.getUserSession();
		this.mainPanel = mainPanel;
		this.resourceManager = resourceManager;
		
		navPanel = new NavigationPanel(this, collapsePanel);
		collapsePanel.setLeftPanel(navPanel);
		
		workspacePanel = new WorkspacePanel(this, userSession.getRlibs(), userSession.isCopyPasteEnabled(), this.mainPanel);
		collapsePanel.setCenterPanel(workspacePanel);
		workspacePanel.getElement().getStyle().setOverflow(Overflow.AUTO);
	}

	public LogRPanel getLogPanel() {
		return logPanel;
	}

	public NavigationPanel getNavigationPanel() {
		return navPanel;
	}

	public void refresh(AirProject project, RScript script, RScriptModel model, List<RScriptModel> versions) {
		workspacePanel.refresh(project, script, model, versions);
	}

	public User getUser() {
		return userSession.getInfoUser().getUser();
	}

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void onScriptDelete(RScript script) {
		workspacePanel.onScriptDelete(script);
	}
	
	
	@Override
	public void onCollapse(boolean collapse) {
		workspacePanel.onResize();
	}

	public WorkspacePanel getWorkspacePanel() {
		return workspacePanel;
	}
}
