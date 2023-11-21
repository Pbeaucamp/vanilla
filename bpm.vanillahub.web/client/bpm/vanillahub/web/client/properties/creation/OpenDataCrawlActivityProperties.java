package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimplePanel;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.properties.CkanProperties;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.vanillahub.core.beans.activities.OpenDataCrawlActivity;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.attribute.ODSProperties;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.Cible.TypeCible;

public class OpenDataCrawlActivityProperties extends PropertiesPanel<Activity> implements IManager<Cible> {

	private ResourceManager resourceManager;
	private OpenDataCrawlActivity activity;

	private PropertiesListBox lstTypeService;
	private PropertiesText txtUrl;

	private List<Cible> cibles;
	private List<Cible> filterCibles;
	private PropertiesListBox lstCibles;
//	private PropertiesListBox lstPackages;
//	private List<CkanPackage> packages;

	private SimplePanel panelDefinition;
	private CkanProperties panelCkan;
	private ODSProperties panelOds;
	
	private CheckBox chkUpdateDataset;
	private CheckBox chkManageResource;

	public OpenDataCrawlActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, OpenDataCrawlActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		int i = 0;
		for (TypeOpenData typeService : TypeOpenData.values()) {
			items.add(new ListItem(getTypeOpenDataName(typeService), typeService.getType()));

			if (activity.getTypeOpenData() != null && activity.getTypeOpenData() == typeService) {
				selectedIndex = i;
			}
			i++;
		}

		lstTypeService = addList(Labels.lblCnst.SelectDataService(), items, WidgetWidth.PCT, changeTypeService, null);
		lstTypeService.setSelectedIndex(selectedIndex);

		txtUrl = addText(Labels.lblCnst.OpenDataUrl(), activity.getUrl(), WidgetWidth.PCT, false);
		
		addVariableText(Labels.lblCnst.LastHarvestDate(), activity.getLastHarvestDateVS(), WidgetWidth.PCT, null);
		addCheckbox(Labels.lblCnst.AddDatePrefixToDatasets(), activity.addPrefix(), addPrefixChangeHandler);

		panelDefinition = addSimplePanel(false);

		this.cibles = resourceManager.getCibles();
		this.filterCibles = filterCibles(activity.getTypeOpenData(), cibles);
		
//		Cible selectedCible = null;

		items = new ArrayList<ListItem>();
		selectedIndex = -1;
		if (filterCibles != null) {
			i = 0;
			for (Cible cible : filterCibles) {
				items.add(new ListItem(cible.getName(), cible.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
//					selectedCible = cible;
				}
				i++;
			}
		}

		lstCibles = addList(LabelsCommon.lblCnst.SelectCible(), items, WidgetWidth.PCT, changeCible, refreshCiblesHandler);
		lstCibles.setSelectedIndex(selectedIndex);

		chkUpdateDataset = addCheckbox(Labels.lblCnst.UpdateDataset(), activity.isUpdateDataset(), updateDatasetChangeHandler);
		chkManageResource = addCheckbox(Labels.lblCnst.ManageResource(), activity.isManageResource(), manageResourceChangeHandler);

//		lstPackages = addList(Labels.lblCnst.SelectDataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, changeDataset, refreshHandler);
//		if (selectedCible != null) {
//			loadPackages(selectedCible, activity.getSelectedPackage());
//		}

		updateUi(activity.getTypeOpenData());
		updateUiDataset(activity.isUpdateDataset());

		refresh();
	}

	private List<Cible> filterCibles(TypeOpenData typeOpenData, List<Cible> cibles) {
		List<Cible> filterCibles = new ArrayList<Cible>();
		switch (typeOpenData) {
		case CKAN:
		case D4C:
			if (cibles != null) {
				for (Cible cible : cibles) {
					if (cible.getType() == TypeCible.D4C) {
						filterCibles.add(cible);
					}
				}
			}
			break;
		case DATA_GOUV:
			if (cibles != null) {
				for (Cible cible : cibles) {
					filterCibles.add(cible);
				}
			}
			break;
		case ODS:
			if (cibles != null) {
				for (Cible cible : cibles) {
					if (cible.getType() == TypeCible.CKAN) {
						filterCibles.add(cible);
					}
				}
			}
			break;

		default:
			break;
		}
		return filterCibles;
	}

//	private void loadPackages(Cible cible, final CkanPackage selectedCkanPackage) {
//		String url = cible.getUrlDisplay();
//		String organisation = cible.getOrg();
//
//		CommonService.Connect.getInstance().getCkanDatasets(url, organisation, new GwtCallbackWrapper<List<CkanPackage>>(this, true, true) {
//
//			@Override
//			public void onSuccess(List<CkanPackage> result) {
//				packages = result;
//
//				lstPackages.clear();
//				if (result != null) {
//					int index = 0;
//					Integer selectedIndex = null;
//					for (CkanPackage pack : result) {
//						lstPackages.addItem(pack.getTitle(), pack.getId());
//
//						if (selectedCkanPackage != null && selectedCkanPackage.getId() != null && selectedCkanPackage.getId().equals(pack.getId())) {
//							selectedIndex = index;
//						}
//
//						index++;
//					}
//
//					if (selectedIndex != null) {
//						lstPackages.setSelectedIndex(selectedIndex);
//					}
//
//					CkanPackage selectedPackage = getCkanPackage();
//					if (selectedPackage != null) {
//						activity.setSelectedPackage(selectedPackage);
//					}
//				}
//			}
//		}.getAsyncCallback());
//	}

	private String getTypeOpenDataName(TypeOpenData typeOpenData) {
		switch (typeOpenData) {
		case DATA_GOUV:
			return Labels.lblCnst.DataGouv();
		case D4C:
			return LabelsCommon.lblCnst.D4C();
		case CKAN:
			return Labels.lblCnst.CKAN();
		case ODS:
			return Labels.lblCnst.OpenDataSoft();
		default:
			return LabelsCommon.lblCnst.Unknown();
		}
	}

	private ChangeHandler changeTypeService = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
			TypeOpenData typeService = TypeOpenData.valueOf(typeServiceId);

			activity.setTypeOpenData(typeService);
			updateUi(typeService);
		}
	};

	// private ClickHandler defineUrlHandler = new ClickHandler() {
	//
	// @Override
	// public void onClick(ClickEvent event) {
	// String openDataUrl = txtUrl.getText();
	//
	// switch (activity.getTypeOpenData()) {
	// case DATA_GOUV:
	// // BuildDataGouvDialog dial = new
	// BuildDataGouvDialog(OpenDataCrawlActivityProperties.this,
	// activity.getDataGouv(), openDataUrl);
	// // dial.center();
	// break;
	//
	// default:
	// break;
	// }
	// }
	// };

//	private ClickHandler refreshHandler = new ClickHandler() {
//
//		@Override
//		public void onClick(ClickEvent event) {
//			refresh();
//		}
//	};
	
	private ClickHandler refreshCiblesHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadCibles(OpenDataCrawlActivityProperties.this, OpenDataCrawlActivityProperties.this);
		}
	};

	private void refresh() {
		String openDataUrl = txtUrl.getText();
		if (openDataUrl != null && !openDataUrl.isEmpty()) {
			switch (activity.getTypeOpenData()) {
			case DATA_GOUV:
				// if (panelDataGouv != null) {
				// panelDataGouv.refresh(openDataUrl, activity.getDataGouv(),
				// true);
				// }
				break;
			case CKAN:
				break;

			default:
				break;
			}
		}
	}

	private void updateUi(TypeOpenData typeService) {
		if (typeService != null) {
			switch (typeService) {
			case CKAN:
			case D4C:
				if (panelCkan == null) {
					bpm.vanillahub.core.beans.activities.attributes.CkanProperties properties = activity.getProperties() != null && activity.getProperties() instanceof bpm.vanillahub.core.beans.activities.attributes.CkanProperties ? (bpm.vanillahub.core.beans.activities.attributes.CkanProperties) activity.getProperties() : null;
					String organisation = properties != null ? properties.getOrganisation() : null;
					String apiKey = properties != null ? properties.getApiKey() : null;

					panelCkan = new CkanProperties(this, organisation, apiKey);
				}
				panelDefinition.setWidget(panelCkan);
				
				chkUpdateDataset.setEnabled(true);
				chkManageResource.setEnabled(true);
				updateUiDataset(false);
				break;
			case ODS:
			case DATA_GOUV:
				if (panelOds == null) {
					bpm.vanillahub.core.beans.activities.attributes.ODSProperties properties = activity.getProperties() != null && activity.getProperties() instanceof bpm.vanillahub.core.beans.activities.attributes.ODSProperties ? (bpm.vanillahub.core.beans.activities.attributes.ODSProperties) activity.getProperties() : null;
					Integer limit = properties != null ? properties.getLimit() : null;
					boolean crawlOneDataset = properties != null ? properties.isCrawlOneDataset() : false;
					String datasetId = properties != null ? properties.getDatasetId() : null;
					String parameters = properties != null ? properties.getQuery() : null;
					
					panelOds = new ODSProperties(this, limit, crawlOneDataset, datasetId, parameters);
				}
				panelDefinition.setWidget(panelOds);
				
				chkUpdateDataset.setEnabled(true);
				chkManageResource.setEnabled(false);
				break;
			default:
				break;
			}
			
			loadResources(cibles);
		}
	}

	private void updateUiDataset(boolean updateDataset) {
//		activity.setUpdateDataset(updateDataset);
//		lstPackages.setVisible(updateDataset);
	}

	// public void refreshGeneratedUrl(String generatedUrl, DataGouv dataGouv) {
	// activity.setDataGouv(dataGouv);
	// txtUrl.setText(generatedUrl);
	// refresh();
	// }

	@Override
	public void loadResources(List<Cible> cibles) {
		this.filterCibles = filterCibles(activity.getTypeOpenData(), cibles);

		lstCibles.clear();
		int selectedIndex = -1;
		if (filterCibles != null) {
			int i = 0;
			for (Cible resource : filterCibles) {
				lstCibles.addItem(resource.getName(), String.valueOf(resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCibles.setSelectedIndex(selectedIndex);
	}

	private Cible findCible(int cibleId) {
		if (filterCibles != null) {
			for (Cible cible : filterCibles) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeCible = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstCibles.getValue(lstCibles.getSelectedIndex()));
			if (cibleId > 0) {
				Cible cible = findCible(cibleId);
				activity.setResource(cible);
				
//				loadPackages(cible, activity.getSelectedPackage());
			}
		}
	};

//	private ChangeHandler changeDataset = new ChangeHandler() {
//
//		@Override
//		public void onChange(ChangeEvent event) {
//			CkanPackage selectedPackage = getCkanPackage();
//			if (selectedPackage != null) {
//				activity.setSelectedPackage(selectedPackage);
//			}
//		}
//	};
//	
//	private CkanPackage getCkanPackage() {
//		return packages.size() > lstPackages.getSelectedIndex() ? packages.get(lstPackages.getSelectedIndex()) : null;
//	}

	@Override
	public boolean isValid() {
		int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
		TypeOpenData typeService = TypeOpenData.valueOf(typeServiceId);

		switch (typeService) {
		case DATA_GOUV:
			// return panelDataGouv.isValid();
			break;
		case CKAN:
		case D4C:
			return panelCkan.getOrg() != null && !panelCkan.getOrg().isEmpty();
		case ODS:
			return panelOds.isValid();
		default:
			break;
		}

		return false;
	}

	@Override
	public Activity buildItem() {
		String url = txtUrl.getText();
		activity.setUrl(url);

		int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
		TypeOpenData typeService = TypeOpenData.valueOf(typeServiceId);

		switch (typeService) {
		case DATA_GOUV:
			// activity.setDataGouv(panelDataGouv.getDataGouv());
			break;
		case CKAN:
		case D4C:
			String org = panelCkan.getOrg();
			String apiKey = panelCkan.getApiKey();

			activity.setProperties(new bpm.vanillahub.core.beans.activities.attributes.CkanProperties(org, apiKey));
			break;
		case ODS:
			Integer limit = panelOds.getLimit();
			boolean crawlOneDataset = panelOds.isCrawlOneDataset();
			String datasetId = panelOds.getDatasetId();
			String parameters = panelOds.getParameters();

			activity.setProperties(new bpm.vanillahub.core.beans.activities.attributes.ODSProperties(limit, crawlOneDataset, datasetId, parameters));
		default:
			break;
		}

		return activity;
	}

	private ValueChangeHandler<Boolean> addPrefixChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setAddPrefix(event.getValue());
		}
	};
	
	private ValueChangeHandler<Boolean> updateDatasetChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setUpdateDataset(event.getValue());
			updateUiDataset(event.getValue());
		}
	};
	
	private ValueChangeHandler<Boolean> manageResourceChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setManageResource(event.getValue());
			updateUiDataset(event.getValue());
		}
	};

	@Override
	public void loadResources() { }
}
