package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesButton;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanillahub.core.beans.activities.CrawlActivity;
import bpm.vanillahub.core.beans.activities.CrawlActivity.TypeCrawl;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.DisplayReferenceDialog;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.resources.Cible;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class CrawlActivityProperties extends PropertiesPanel<Activity> implements IManager<Cible> {

	private ResourceManager resourceManager;
	private CrawlActivity activity;
	
	private PropertiesButton btnReference;
	private VariablePropertiesText txtOutputFile, txtUrl, txtNutchUrl, txtStart, txtEnd, txtRegex, txtStartWith;
	private PropertiesText txtDepth;
	private PropertiesListBox lstResources;
	
	private List<Cible> resources;
	
	public CrawlActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, CrawlActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.activity = activity;
		this.resourceManager = (ResourceManager) resourceManager;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedActionIndex = -1;
		for (TypeCrawl type : TypeCrawl.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeCrawl() == type) {
				selectedActionIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(Labels.lblCnst.SelectCrawlType(), actions, WidgetWidth.PCT, changeTypeCrawl, null);
		if (selectedActionIndex != -1) {
			lstType.setSelectedIndex(selectedActionIndex);
		}

		txtOutputFile = addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputFileVS(), WidgetWidth.PCT, null);
		txtUrl = addVariableText(LabelsCommon.lblCnst.URL(), activity.getUrlVS(), WidgetWidth.PCT, null);
		
		txtNutchUrl = addVariableText(Labels.lblCnst.NutchUrl(), activity.getNutchUrlVS(), WidgetWidth.PCT, null);
		
		btnReference = addButton(Labels.lblCnst.ShowDialogReferenceHelper(), loadReference);
		
		txtStart = addVariableText(Labels.lblCnst.StartTag(), activity.getBaliseStartVS(), WidgetWidth.PCT, null);
		txtEnd = addVariableText(Labels.lblCnst.EndTag(), activity.getBaliseEndVS(), WidgetWidth.PCT, null);
	
		this.resources = this.resourceManager.getCibles();

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (resources != null) {
			i = 0;
			for (Cible resource : resources) {
				items.add(new ListItem(resource.getName(), resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstResources = addList(LabelsCommon.lblCnst.SelectCible(), items, WidgetWidth.PCT, changeResource, refreshHandler);
		lstResources.setSelectedIndex(selectedIndex);
		
		txtRegex = addVariableText(Labels.lblCnst.Regex(), activity.getRegexVS(), WidgetWidth.PCT, null);
		txtStartWith = addVariableText(Labels.lblCnst.UrlStartWith(), activity.getStartWithVS(), WidgetWidth.PCT, null);
		
		txtDepth = addVariableText(Labels.lblCnst.Depth(), activity.getDepthVS(), WidgetWidth.PCT, null);
		
		updateUi(activity.getTypeCrawl());
	}
	
	private void updateUi(TypeCrawl type) {
		if (type == TypeCrawl.EXTRACT) {
			txtOutputFile.setVisible(true);
			btnReference.setVisible(true);
			txtStart.setVisible(true);
			txtEnd.setVisible(true);
			
			txtNutchUrl.setVisible(false);
			txtDepth.setVisible(false);
			
			lstResources.setVisible(false);
			txtRegex.setVisible(false);
			txtStartWith.setVisible(false);
		}
		else if (type == TypeCrawl.NUTCH) {
			txtOutputFile.setVisible(false);
			btnReference.setVisible(false);
			txtStart.setVisible(false);
			txtEnd.setVisible(false);
			
			txtNutchUrl.setVisible(true);
			txtDepth.setVisible(true);
			
			lstResources.setVisible(false);
			txtRegex.setVisible(false);
			txtStartWith.setVisible(false);
		}
		else if (type == TypeCrawl.CRAWL4J) {
			txtOutputFile.setVisible(false);
			btnReference.setVisible(false);
			txtStart.setVisible(false);
			txtEnd.setVisible(false);
			
			txtNutchUrl.setVisible(false);
			
			lstResources.setVisible(true);
			txtRegex.setVisible(true);
			txtStartWith.setVisible(true);
			txtDepth.setVisible(true);
		}
	}

	private Cible findResource(int cibleId) {
		if (resources != null) {
			for (Cible resource : resources) {
				if (resource.getId() == cibleId) {
					return resource;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		if (txtDepth.getText().isEmpty()) {
			isValid = false;
			txtDepth.setTxtError(Labels.lblCnst.DepthIsNeeded());
		}
		else {
			try {
				Integer.parseInt(txtDepth.getText());
				txtDepth.setTxtError("");
			} catch(Exception e) {
				isValid = false;
				txtDepth.setText("1");
				txtDepth.setTxtError(Labels.lblCnst.DepthIsInteger());
			}
		}
		return isValid;
	}

	@Override
	public void loadResources(List<Cible> result) {
		this.resources = result;

		lstResources.clear();
		int selectedIndex = -1;
		if (resources != null) {
			int i = 0;
			for (Cible resource : resources) {
				lstResources.addItem(resource.getName(), String.valueOf(resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstResources.setSelectedIndex(selectedIndex);
	}

	@Override
	public Activity buildItem() {
		return activity;
	}

	private ChangeHandler changeTypeCrawl = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeCrawl(TypeCrawl.valueOf(type));
			updateUi(activity.getTypeCrawl());
		}
	};
	
	private ClickHandler loadReference = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String url = txtUrl.getVariableText().getStringForTextbox();
			if (url.isEmpty()) {
				txtUrl.setTxtError(LabelsCommon.lblCnst.UrlIsNeeded());
				return;
			}

			DisplayReferenceDialog dial = new DisplayReferenceDialog(url);
			dial.center();
		}
	};

	private ChangeHandler changeResource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int resourceId = Integer.parseInt(lstResources.getValue(lstResources.getSelectedIndex()));
			if (resourceId > 0) {
				activity.setResource(findResource(resourceId));
			}
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadCibles(CrawlActivityProperties.this, CrawlActivityProperties.this);
		}
	};

	@Override
	public void loadResources() { }
}
