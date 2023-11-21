package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanillahub.core.beans.activities.DataServiceActivity;
import bpm.vanillahub.core.beans.activities.DataServiceActivity.TypeService;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties;
import bpm.vanillahub.core.beans.activities.attributes.EbayFinding;
import bpm.vanillahub.core.beans.activities.attributes.Quandl;
import bpm.vanillahub.core.beans.activities.attributes.URLProperties;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.attribute.APIPropertiesPanel;
import bpm.vanillahub.web.client.properties.creation.attribute.EbayFindingProperties;
import bpm.vanillahub.web.client.properties.creation.attribute.QuandlProperties;
import bpm.vanillahub.web.client.properties.creation.attribute.URLPropertiesPanel;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class DataServiceActivityProperties extends PropertiesPanel<Activity> implements IManager<Void> {

	private ResourceManager resourceManager;
	private DataServiceActivity activity;

	private Label lblGeneratedUrl;
	private PropertiesListBox lstTypeService;
	private SimplePanel panelDefinition;

	private QuandlProperties panelQuandl;
//	private YahooFinanceProperties panelYahooFinance;
//	private YahooWeatherProperties panelYahooWeather;
	private EbayFindingProperties panelEbayFinding;
	private URLPropertiesPanel panelUrl;
	private APIPropertiesPanel panelAPI;

	public DataServiceActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, DataServiceActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		int i = 0;
		for (TypeService typeService : TypeService.values()) {
			items.add(new ListItem(getTypeServiceName(typeService), typeService.getType()));

			if (activity.getTypeService() != null && activity.getTypeService() == typeService) {
				selectedIndex = i;
			}
			i++;
		}

		lstTypeService = addList(Labels.lblCnst.SelectDataService(), items, WidgetWidth.PCT, changeTypeService, null);
		lstTypeService.setSelectedIndex(selectedIndex);

		lblGeneratedUrl = addLabel();
		panelDefinition = addSimplePanel(false);

		updateUi(activity.getTypeService());
	}

	private String getTypeServiceName(TypeService typeService) {
		switch (typeService) {
		case QUANDL:
			return Labels.lblCnst.Quandl();
		case EBAY_FINDING:
			return Labels.lblCnst.EbayFinding();
		case URL:
			return LabelsCommon.lblCnst.URL();
		case API:
			return Labels.lblCnst.API();
		default:
			return LabelsCommon.lblCnst.Unknown();
		}
	}

	private ChangeHandler changeTypeService = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
			TypeService typeService = TypeService.valueOf(typeServiceId);

			activity.setTypeService(typeService);
			updateUi(typeService);
		}
	};

	private void updateUi(TypeService typeService) {
		if (typeService != null) {
			switch (typeService) {
			case QUANDL:
				if (panelQuandl == null) {
					Quandl quandl = activity.getAttribute() != null && activity.getAttribute() instanceof Quandl ? (Quandl) activity.getAttribute() : null;
					panelQuandl = new QuandlProperties(this, resourceManager, quandl);
				}
				registerPanelWithVariables(panelQuandl);
				panelDefinition.setWidget(panelQuandl);
				break;
//			case YAHOO_FINANCE:
//				if (panelYahooFinance == null) {
//					YahooFinance yahooFinance = activity.getAttribute() != null && activity.getAttribute() instanceof YahooFinance ? (YahooFinance) activity.getAttribute() : null;
//					panelYahooFinance = new YahooFinanceProperties(this, resourceManager, yahooFinance);
//				}
//				registerPanelWithVariables(panelYahooFinance);
//				panelDefinition.setWidget(panelYahooFinance);
//				break;
//			case YAHOO_WEATHER:
//				if (panelYahooWeather == null) {
//					YahooWeather yahooWeather = activity.getAttribute() != null && activity.getAttribute() instanceof YahooWeather ? (YahooWeather) activity.getAttribute() : null;
//					panelYahooWeather = new YahooWeatherProperties(this, resourceManager, yahooWeather);
//				}
//				registerPanelWithVariables(panelYahooWeather);
//				panelDefinition.setWidget(panelYahooWeather);
//				break;
			case EBAY_FINDING:
				if (panelEbayFinding == null) {
					EbayFinding ebayFinding = activity.getAttribute() != null && activity.getAttribute() instanceof EbayFinding ? (EbayFinding) activity.getAttribute() : null;
					panelEbayFinding = new EbayFindingProperties(this, resourceManager, ebayFinding);
				}
				registerPanelWithVariables(panelEbayFinding);
				panelDefinition.setWidget(panelEbayFinding);
				break;
			case URL:
				if (panelUrl == null) {
					URLProperties urlProperties = activity.getAttribute() != null && activity.getAttribute() instanceof URLProperties ? (URLProperties) activity.getAttribute() : null;
					panelUrl = new URLPropertiesPanel(resourceManager, urlProperties);
				}
				registerPanelWithVariables(panelUrl);
				panelDefinition.setWidget(panelUrl);
				break;
			case API:
				if (panelAPI == null) {
					APIProperties urlProperties = activity.getAttribute() != null && activity.getAttribute() instanceof APIProperties ? (APIProperties) activity.getAttribute() : null;
					panelAPI = new APIPropertiesPanel(resourceManager, urlProperties);
				}
				registerPanelWithVariables(panelAPI);
				panelDefinition.setWidget(panelAPI);
				break;

			default:
				break;
			}
		}
	}

	public void refreshGeneratedUrl(String generatedUrl) {
		lblGeneratedUrl.setText(generatedUrl);
	}

	@Override
	public void loadResources(List<Void> result) {

	}

	@Override
	public void loadResources() {
	}

	@Override
	public boolean isValid() {
		int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
		TypeService typeService = TypeService.valueOf(typeServiceId);

		switch (typeService) {
		case QUANDL:
			return panelQuandl.isValid();
//		case YAHOO_FINANCE:
//			return panelYahooFinance.isValid();
//		case YAHOO_WEATHER:
//			return panelYahooWeather.isValid();
		case EBAY_FINDING:
			return panelEbayFinding.isValid();
		case URL:
			return panelUrl.isValid();
		case API:
			return panelAPI.isValid();
		default:
			break;
		}

		return false;
	}

	@Override
	public Activity buildItem() {
		int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
		TypeService typeService = TypeService.valueOf(typeServiceId);

		switch (typeService) {
		case QUANDL:
			activity.setAttribute(panelQuandl.getQuandl());
			break;
//		case YAHOO_FINANCE:
//			activity.setAttribute(panelYahooFinance.getYahooFinance());
//			break;
//		case YAHOO_WEATHER:
//			activity.setAttribute(panelYahooWeather.getYahooWeather());
//			break;
		case EBAY_FINDING:
			activity.setAttribute(panelEbayFinding.getEbayFinding());
			break;
		case URL:
			activity.setAttribute(panelUrl.getUrlProperties());
			break;
		case API:
			activity.setAttribute(panelAPI.getUrlProperties());
			break;

		default:
			break;
		}

		return activity;
	}
}
