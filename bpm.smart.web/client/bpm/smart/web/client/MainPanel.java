package bpm.smart.web.client;

import bpm.gwt.commons.client.dialog.FeedbackDialog;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.popup.IChangeView;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.TopToolbar;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.LogRPanel;
import bpm.smart.web.client.panels.WorkflowDisplayPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel implements IChangeView {
	
	private static final int CLASSIC_TOP_TOOLBAR = 70;
	private static final int CLASSIC_FOOTER = 30;
	
	private static final int FULL_SCREEN_TOP_TOOLBAR = 25;
	private static final int FULL_SCREEN_FOOTER = 0;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	SimplePanel toolbarPanel, footerPanel, mainPanel, navPanel;

	private Bpm_smart_web parentPanel;
	private WorkflowDisplayPanel workflowDisplayPanel;
	
	private UserSession userSession;

	public MainPanel(Bpm_smart_web parentPanel, UserSession userSession) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentPanel = parentPanel;
		this.userSession = userSession;

		buildToolbar();
		buildFooterPanel();
		buildMainPanel();

		this.addStyleName(VanillaCSS.BODY_BACKGROUND);

		InfoUser infoUser = userSession.getInfoUser();
		if (infoUser.feedbackIsNotInited()) {
			FeedbackDialog dial = new FeedbackDialog();
			dial.center();
		}
	}

	private void buildToolbar() {
		TopToolbar topToolbar = new TopToolbar(LabelsConstants.lblCnst.TitleApplication(), null, parentPanel, this, parentPanel, parentPanel.getInfoUser());
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		toolbarPanel.setWidget(topToolbar);
	}

	private void buildFooterPanel() {
		footerPanel.addStyleName(VanillaCSS.BOTTOM);
	}

	private void buildMainPanel() {
		workflowDisplayPanel = new WorkflowDisplayPanel(this, getUserSession().getInfoUser());
		mainPanel.setWidget(workflowDisplayPanel);
		
		dockPanel.setWidgetSize(navPanel, 0);
	}

	@Override
	public void switchView(TypeView typeView) {
		switch (typeView) {
		case CLASSIC_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, CLASSIC_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, CLASSIC_FOOTER);
			break;
		case FULL_SCREEN_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, FULL_SCREEN_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, FULL_SCREEN_FOOTER);
			break;

		default:
			break;
		}
	}

	public UserSession getUserSession() {
		return userSession;
	}
	
	public LogRPanel getLogPanel() {
		return workflowDisplayPanel.getLogPanel();
	}
	
	public WorkflowDisplayPanel getWorkflowDisplayPanel() {
		return workflowDisplayPanel;
	}
}
