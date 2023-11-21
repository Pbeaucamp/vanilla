package bpm.vanillahub.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.CreationPanel;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanillahub.core.beans.activities.ActionActivity;
import bpm.vanillahub.core.beans.activities.AklaboxActivity;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.core.beans.activities.CompressionActivity;
import bpm.vanillahub.core.beans.activities.ConnectorXmlActivity;
import bpm.vanillahub.core.beans.activities.CrawlActivity;
import bpm.vanillahub.core.beans.activities.DataServiceActivity;
import bpm.vanillahub.core.beans.activities.EncryptionActivity;
import bpm.vanillahub.core.beans.activities.GeojsonActivity;
import bpm.vanillahub.core.beans.activities.LimeSurveyInputActivity;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.core.beans.activities.MdmActivity;
import bpm.vanillahub.core.beans.activities.MdmInputActivity;
import bpm.vanillahub.core.beans.activities.MergeFilesActivity;
import bpm.vanillahub.core.beans.activities.OpenDataActivity;
import bpm.vanillahub.core.beans.activities.OpenDataCrawlActivity;
import bpm.vanillahub.core.beans.activities.PreClusterGeoDataActivity;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity;
import bpm.vanillahub.core.beans.activities.SourceActivity;
import bpm.vanillahub.core.beans.activities.ValidationActivity;
import bpm.vanillahub.web.client.properties.creation.ActionActivityProperties;
import bpm.vanillahub.web.client.properties.creation.AklaboxActivityProperties;
import bpm.vanillahub.web.client.properties.creation.CibleActivityProperties;
import bpm.vanillahub.web.client.properties.creation.CompressionActivityProperties;
import bpm.vanillahub.web.client.properties.creation.ConnectorXmlActivityProperties;
import bpm.vanillahub.web.client.properties.creation.CrawlActivityProperties;
import bpm.vanillahub.web.client.properties.creation.DataServiceActivityProperties;
import bpm.vanillahub.web.client.properties.creation.EncryptionActivityProperties;
import bpm.vanillahub.web.client.properties.creation.GeojsonActivityProperties;
import bpm.vanillahub.web.client.properties.creation.LimeSurveyInputActivityProperties;
import bpm.vanillahub.web.client.properties.creation.MailActivityProperties;
import bpm.vanillahub.web.client.properties.creation.MdmActivityProperties;
import bpm.vanillahub.web.client.properties.creation.MdmInputActivityProperties;
import bpm.vanillahub.web.client.properties.creation.MergeFilesActivityProperties;
import bpm.vanillahub.web.client.properties.creation.OpenDataActivityProperties;
import bpm.vanillahub.web.client.properties.creation.OpenDataCrawlActivityProperties;
import bpm.vanillahub.web.client.properties.creation.PreClusterGeoDataActivityProperties;
import bpm.vanillahub.web.client.properties.creation.RunVanillaItemActivityProperties;
import bpm.vanillahub.web.client.properties.creation.SocialNetworkActivityProperties;
import bpm.vanillahub.web.client.properties.creation.SourceActivityProperties;
import bpm.vanillahub.web.client.properties.creation.ValidationActivityProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.vanillahub.web.client.tabs.resources.ResourcesPanel;
import bpm.vanillahub.web.client.tabs.workflow.BoxHelper;
import bpm.vanillahub.web.client.tabs.workflow.ViewerPanel;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;

public class ContentDisplayPanel extends Composite implements IWorkflowAppManager, TabManager {

	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, ContentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String displayNone();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel tabHeaderPanel;

	@UiField
	HTMLPanel contentPanel;

	private InfoUser infoUser;
	private ResourceManager manager;

	private ViewerPanel consultPanel;
	private ResourcesPanel resourcesPanel;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	public ContentDisplayPanel(IWait waitPanel, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;

		manager = new ResourceManager();

		displayConsultation(false);
		if (infoUser.getUser().isSuperUser()) {
			displayResources(false);
		}
	}

	public void displayConsultation(boolean reload) {
		if (consultPanel == null) {
			consultPanel = new ViewerPanel(this, infoUser);
		}

		changeTab(consultPanel, true);

		if (reload) {
			consultPanel.loadWorkflows();
		}
	}
	
	@Override
	public void reloadConsult() {
		consultPanel.loadWorkflows();
	}

	public void displayResources(boolean select) {
		if (resourcesPanel == null) {
			resourcesPanel = new ResourcesPanel(this, manager, infoUser.isConnectedToVanilla());
		}

		changeTab(resourcesPanel, select);
	}

	private void changeTab(Tab selectedPanel, boolean select) {
		if (select && selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (select && this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
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

	private void updateSize(List<AbstractTabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (AbstractTabHeader tabHeader : openTabs) {
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

	@Override
	public InfoUser getInfoUser() {
		return infoUser;
	}

	@Override
	public ResourceManager getResourceManager() {
		return manager;
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
	public Activity buildActivity(TypeActivity type, String name) {
		switch (type) {
		case START:
			return new StartActivity(name);
		case STOP:
			return new StopActivity(name);
		case CIBLE:
			return new CibleActivity(name);
		case COMPRESSION:
			return new CompressionActivity(name);
		case ENCRYPTAGE:
			return new EncryptionActivity(name);
		case MAIL:
			return new MailActivity(name);
		case SOURCE:
			return new SourceActivity(name);
		case VALIDATION:
			return new ValidationActivity(name);
		case CONNECTOR:
			return new ConnectorXmlActivity(name);
		case ACTION:
			return new ActionActivity(name);
		case DATA_SERVICE:
			return new DataServiceActivity(name);
		case VANILLA_ITEM:
			return new RunVanillaItemActivity(name);
		case CRAWL:
			return new CrawlActivity(name);
		case SOCIAL:
			return new SocialNetworkActivity(name);
		case OPEN_DATA:
			return new OpenDataActivity(name);
		case MDM:
			return new MdmActivity(name);
		case AKLABOX:
			return new AklaboxActivity(name);
		case GEOJSON:
			return new GeojsonActivity(name);
		case PRECLUSTER:
			return new PreClusterGeoDataActivity(name);
		case OPENDATA_CRAWL:
			return new OpenDataCrawlActivity(name);
		case MDM_INPUT:
			return new MdmInputActivity(name);
		case LIMESURVEY_INPUT:
			return new LimeSurveyInputActivity(name);
		case MERGE_FILES:
			return new MergeFilesActivity(name);
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
		case CIBLE:
			return new CibleActivityProperties(getResourceManager(), creationPanel, boxItem, (CibleActivity) activity);
		case COMPRESSION:
			return new CompressionActivityProperties(getResourceManager(), creationPanel, boxItem, (CompressionActivity) activity);
		case ENCRYPTAGE:
			return new EncryptionActivityProperties(getResourceManager(), creationPanel, boxItem, (EncryptionActivity) activity);
		case MAIL:
			return new MailActivityProperties(getResourceManager(), creationPanel, boxItem, (MailActivity) activity);
		case SOURCE:
			return new SourceActivityProperties(getResourceManager(), creationPanel, boxItem, (SourceActivity) activity);
		case VALIDATION:
			return new ValidationActivityProperties(getResourceManager(), creationPanel, boxItem, (ValidationActivity) activity);
		case CONNECTOR:
			return new ConnectorXmlActivityProperties(getResourceManager(), creationPanel, boxItem, (ConnectorXmlActivity) activity);
		case ACTION:
			return new ActionActivityProperties(getResourceManager(), creationPanel, boxItem, (ActionActivity) activity);
		case DATA_SERVICE:
			return new DataServiceActivityProperties(getResourceManager(), creationPanel, boxItem, (DataServiceActivity) activity);
		case VANILLA_ITEM:
			return new RunVanillaItemActivityProperties(getResourceManager(), creationPanel, boxItem, (RunVanillaItemActivity) activity);
		case CRAWL:
			return new CrawlActivityProperties(getResourceManager(), creationPanel, boxItem, (CrawlActivity) activity);
		case SOCIAL:
			return new SocialNetworkActivityProperties(getResourceManager(), creationPanel, boxItem, (SocialNetworkActivity) activity);
		case OPEN_DATA:
			return new OpenDataActivityProperties(getResourceManager(), creationPanel, boxItem, (OpenDataActivity) activity);
		case MDM:
			return new MdmActivityProperties(getResourceManager(), creationPanel, boxItem, (MdmActivity) activity);
		case AKLABOX:
			return new AklaboxActivityProperties(getResourceManager(), creationPanel, boxItem, (AklaboxActivity) activity);
		case GEOJSON:
			return new GeojsonActivityProperties(getResourceManager(), creationPanel, boxItem, (GeojsonActivity) activity);
		case PRECLUSTER:
			return new PreClusterGeoDataActivityProperties(getResourceManager(), creationPanel, boxItem, (PreClusterGeoDataActivity) activity);
		case OPENDATA_CRAWL:
			return new OpenDataCrawlActivityProperties(getResourceManager(), creationPanel, boxItem, (OpenDataCrawlActivity) activity);
		case MDM_INPUT:
			return new MdmInputActivityProperties(getResourceManager(), creationPanel, boxItem, (MdmInputActivity) activity);
		case LIMESURVEY_INPUT:
			return new LimeSurveyInputActivityProperties(getResourceManager(), creationPanel, boxItem, (LimeSurveyInputActivity) activity);
		case MERGE_FILES:
			return new MergeFilesActivityProperties(getResourceManager(), creationPanel, boxItem, (MergeFilesActivity) activity);
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
	public void openViewer(IRepositoryObject item) { }

	@Override
	public List<StackNavigationPanel> getCategories(CollapsePanel collapsePanel) {
		return BoxHelper.createCategories(this, collapsePanel);
	}

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return -1;
	}

	@Override
	public void updatePosition(String tabId, int index) { }

	@Override
	public void postProcess() { }

	public void displayWorkflow(Workflow workflow) {
		CreationPanel creationPanel = new CreationPanel(this, this);

		changeTab(creationPanel, true);

		if (workflow != null) {
			creationPanel.openCreation(workflow);
		}
		else {
			creationPanel.initNewCreation();
		}
	}
}
