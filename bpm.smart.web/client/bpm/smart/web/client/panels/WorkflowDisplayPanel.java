package bpm.smart.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.feedback.FeedbackPanel;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabHeader;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.CreationPanel;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.smart.core.model.workflow.activity.AirScriptActivity;
import bpm.smart.core.model.workflow.activity.ChartOutputActivity;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity;
import bpm.smart.core.model.workflow.activity.DecisionTreeActivity;
import bpm.smart.core.model.workflow.activity.FieldSelectionActivity;
import bpm.smart.core.model.workflow.activity.FileOutputActivity;
import bpm.smart.core.model.workflow.activity.FilterActivity;
import bpm.smart.core.model.workflow.activity.HAClustActivity;
import bpm.smart.core.model.workflow.activity.HeadActivity;
import bpm.smart.core.model.workflow.activity.KmeansActivity;
import bpm.smart.core.model.workflow.activity.RecodeActivity;
import bpm.smart.core.model.workflow.activity.SimpleLinearRegActivity;
import bpm.smart.core.model.workflow.activity.SortingActivity;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.panels.properties.AirScriptActivityProperties;
import bpm.smart.web.client.panels.properties.ChartActivityProperties;
import bpm.smart.web.client.panels.properties.CorMatrixActivityProperties;
import bpm.smart.web.client.panels.properties.DecisionTreeActivityProperties;
import bpm.smart.web.client.panels.properties.FieldSelectionActivityProperties;
import bpm.smart.web.client.panels.properties.FileOutputActivityProperties;
import bpm.smart.web.client.panels.properties.FilterActivityProperties;
import bpm.smart.web.client.panels.properties.HAClustActivityProperties;
import bpm.smart.web.client.panels.properties.HeadActivityProperties;
import bpm.smart.web.client.panels.properties.KmeansActivityProperties;
import bpm.smart.web.client.panels.properties.RecodeActivityProperties;
import bpm.smart.web.client.panels.properties.SimpleLinearRegActivityProperties;
import bpm.smart.web.client.panels.properties.SortingActivityProperties;
import bpm.smart.web.client.panels.resources.ResourceManager;
import bpm.smart.web.client.panels.resources.ResourcesPanel;
import bpm.smart.web.client.panels.workflow.ConsultPanel;
import bpm.smart.web.client.utils.BoxHelper;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowDisplayPanel extends Composite implements TabManager, IWorkflowAppManager {

	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, WorkflowDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String displayNone();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel tabHeaderPanel, contentDisplayPanel;

	@UiField
	HTMLPanel contentPanel;

	private MainPanel mainPanel;
	private ConsultPanel consultPanel;
	private LogRPanel logPanel;
	
	private ResourceManager manager;

	private TabHeader selectedBtn;
	private Tab selectedPanel;

	private List<TabHeader> openTabs = new ArrayList<TabHeader>();

	public WorkflowDisplayPanel(MainPanel mainPanel, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		this.manager = new ResourceManager();
		
		this.logPanel = new LogRPanel(mainPanel);
		contentDisplayPanel.add(logPanel);

		displayConsultation(false);
		displayResources(mainPanel, true);

		if (infoUser.canSendFeedback()) {
			FeedbackPanel panel = new FeedbackPanel(mainPanel, infoUser.getUser());
			contentDisplayPanel.add(panel);
		}
	}

	public void displayConsultation(boolean reload) {
		if (consultPanel == null) {
			consultPanel = new ConsultPanel(this);
		}

		changeTab(consultPanel, true);

		if (reload) {
			consultPanel.loadWorkflows();
		}
	}

	public void displayResources(MainPanel mainPanel, boolean select) {
		ResourcesPanel resourcesPanel = new ResourcesPanel(mainPanel, this);
		changeTab(resourcesPanel, select);
	}
	
	@Override
	public void reloadConsult() {
		consultPanel.loadWorkflows();
	}

	public void displayCreation(Workflow workflow) {
		CreationPanel creationPanel = new CreationPanel(this, this);

		changeTab(creationPanel, true);

		if (workflow != null) {
			creationPanel.openCreation(workflow);
		}
		else {
			creationPanel.initNewCreation();
		}
	}

	private void changeTab(Tab selectedPanel, boolean select) {
		if (select && selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (select && this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		TabHeader header = (TabHeader) selectedPanel.buildTabHeader();
		if (!header.isHeaderOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
			
			header.setHeaderOpen(true);
		}

		if (select) {
			this.selectedBtn = header;
			this.selectedBtn.setSelected(true);

			this.selectedPanel = selectedPanel;
			this.selectedPanel.removeStyleName(style.displayNone());

			if (!selectedPanel.isOpen()) {
				header.setOpen(true);
				this.contentPanel.add(selectedPanel);
			}
			
			selectedPanel.doActionAfterSelection();
		}
	}

	private void updateSize(List<TabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (TabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if (tabNumber > 0) {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
		else {
			return 0;
		}
	}

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changeTab(openTabs.get(index - 1).getTab(), true);
			}
			else {
				changeTab(openTabs.get(0).getTab(), true);
			}
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changeTab(tabHeader.getTab(), true);
	}

	public void openViewer(IRepositoryObject item) { }

	public MainPanel getMainPanel() {
		return mainPanel;
	}
	
	public InfoUser getInfoUser() {
		return mainPanel.getUserSession().getInfoUser();
	}
	
	public LogRPanel getLogPanel() {
		return logPanel;
	}
	
	@Override
	public ImageResource getImage(TypeActivity type, boolean black) {
		return BoxHelper.getImage(type, black);
	}

	@Override
	public String getLabel(TypeActivity type) {
		return BoxHelper.getLabel(type);
	}

	@Override
	public List<StackNavigationPanel> getCategories(CollapsePanel collapsePanel) {
		return BoxHelper.createCategories(this, collapsePanel);
	}

	@Override
	public Activity buildActivity(TypeActivity type, String name) {
		switch (type) {
		case START:
			return new StartActivity(name);
		case STOP:
			return new StopActivity(name);
		case RSCRIPT:
			return new AirScriptActivity(name);
		case RECODE:
			return new RecodeActivity(name);
		case OUTPUT_FILE:
			return new FileOutputActivity(name);
		case CHART:
			return new ChartOutputActivity(name);
		case FIELD_SELECTION:
			return new FieldSelectionActivity(name);
		case HEAD:
			return new HeadActivity(name);
		case SORTING_ACTIVITY:
			return new SortingActivity(name);
		case FILTER_ACTIVITY:
			return new FilterActivity(name);
		case SIMPLE_LINEAR_REG:
			return new SimpleLinearRegActivity(name);
		case HAC:
			return new HAClustActivity(name);
		case CORRELATION_MATRIX:
			return new CorMatrixActivity(name);
		case DECISION_TREE:
			return new DecisionTreeActivity(name);
		case KMEANS:
			return new KmeansActivity(name);
		default:
			break;
		}

		return null;
	}

	@Override
	public PropertiesPanel<Activity> buildActivityProperties(WorkspacePanel creationPanel, BoxItem boxItem, TypeActivity type, Activity activity) {
		switch (type) {
		case START:
			return null;
		case STOP:
			return null;
		case RSCRIPT:
			return new AirScriptActivityProperties(getResourceManager(), creationPanel, boxItem, (AirScriptActivity) activity, getInfoUser().getUser().getId());
		case RECODE:
			return new RecodeActivityProperties(getResourceManager(), creationPanel, boxItem, (RecodeActivity) activity);
		case OUTPUT_FILE:
			return new FileOutputActivityProperties(getResourceManager(), creationPanel, boxItem, (FileOutputActivity) activity);
		case CHART:
			return new ChartActivityProperties(getResourceManager(), creationPanel, boxItem, (ChartOutputActivity) activity);
		case FIELD_SELECTION:
			return new FieldSelectionActivityProperties(getResourceManager(), creationPanel, boxItem, (FieldSelectionActivity) activity);
		case HEAD:
			return new HeadActivityProperties(getResourceManager(), creationPanel, boxItem, (HeadActivity) activity);
		case SORTING_ACTIVITY:
			return new SortingActivityProperties(getResourceManager(), creationPanel, boxItem, (SortingActivity) activity);
		case FILTER_ACTIVITY:
			return new FilterActivityProperties(getResourceManager(), creationPanel, boxItem, (FilterActivity) activity);
		case SIMPLE_LINEAR_REG:
			return new SimpleLinearRegActivityProperties(getResourceManager(), creationPanel, boxItem, (SimpleLinearRegActivity) activity);
		case HAC:
			return new HAClustActivityProperties(getResourceManager(), creationPanel, boxItem, (HAClustActivity) activity);
		case CORRELATION_MATRIX:
			return new CorMatrixActivityProperties(getResourceManager(), creationPanel, boxItem, (CorMatrixActivity) activity);
		case DECISION_TREE:
			return new DecisionTreeActivityProperties(getResourceManager(), creationPanel, boxItem, (DecisionTreeActivity) activity);
		case KMEANS:
			return new KmeansActivityProperties(getResourceManager(), creationPanel, boxItem, (KmeansActivity) activity);
		default:
			break;
		}

		return null;
	}

	@Override
	public void manageWorkflow(Workflow workflow, boolean modify, AsyncCallback<Workflow> callback) {
		WorkflowsService.Connect.getInstance().manageWorkflow(workflow, modify, callback);
	}

	@Override
	public IResourceManager getResourceManager() {
		return manager;
	}

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updatePosition(String tabId, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub
		
	}

}
