package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.vanillahub.core.beans.activities.OpenDataActivity;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.core.beans.activities.attributes.DataGouv;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.BuildDataGouvDialog;
import bpm.vanillahub.web.client.properties.creation.attribute.DataGouvProperties;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

public class OpenDataActivityProperties extends PropertiesPanel<Activity> {

	private OpenDataActivity activity;

	private PropertiesListBox lstTypeService;
	private PropertiesText txtUrl;
	private SimplePanel panelDefinition;

	private DataGouvProperties panelDataGouv;
//	private CkanProperties panelCkan;

	public OpenDataActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, OpenDataActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputNameVS(), WidgetWidth.PCT, null);

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		int i = 0;
		for (TypeOpenData typeService : TypeOpenData.values()) {
			if (typeService == TypeOpenData.DATA_GOUV) {
				items.add(new ListItem(getTypeOpenDataName(typeService), typeService.getType()));
	
				if (activity.getTypeOpenData() != null && activity.getTypeOpenData() == typeService) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstTypeService = addList(Labels.lblCnst.SelectDataService(), items, WidgetWidth.PCT, changeTypeService, null);
		lstTypeService.setSelectedIndex(selectedIndex);

		addButton(Labels.lblCnst.DefineUrl(), defineUrlHandler);

		txtUrl = addText(Labels.lblCnst.OpenDataUrl(), activity.getUrl(), WidgetWidth.PCT, false);

		addButton(LabelsCommon.lblCnst.Refresh(), refreshHandler);

		panelDefinition = addSimplePanel(false);
		updateUi(activity.getTypeOpenData());

		refresh();
	}

	private String getTypeOpenDataName(TypeOpenData typeOpenData) {
		switch (typeOpenData) {
		case DATA_GOUV:
			return Labels.lblCnst.DataGouv();
//		case CKAN:
//			return Labels.lblCnst.D4C();
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

	private ClickHandler defineUrlHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String openDataUrl = txtUrl.getText();
			
			switch (activity.getTypeOpenData()) {
			case DATA_GOUV:
				BuildDataGouvDialog dial = new BuildDataGouvDialog(OpenDataActivityProperties.this, activity.getDataGouv(), openDataUrl);
				dial.center();
				break;

			default:
				break;
			}
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			refresh();
		}
	};

	private void refresh() {
		String openDataUrl = txtUrl.getText();
		if (openDataUrl != null && !openDataUrl.isEmpty()) {
			switch (activity.getTypeOpenData()) {
			case DATA_GOUV:
				if (panelDataGouv != null) {
					panelDataGouv.refresh(openDataUrl, activity.getDataGouv(), true);
				}
				break;
//			case CKAN:
//				if (panelCkan == null) {
//					CkanPackage pack = activity.getCkanPackage() != null ? activity.getCkanPackage() : null;
//					panelCkan = new CkanProperties(this, pack, activity.getUrl());
//				}
//				panelDefinition.setWidget(panelCkan);
//				break;

			default:
				break;
			}
		}
	}

	private void updateUi(TypeOpenData typeService) {
		if (typeService != null) {
			switch (typeService) {
			case DATA_GOUV:
				if (panelDataGouv == null) {
					DataGouv dataGouv = activity.getDataGouv() != null && activity.getDataGouv() instanceof DataGouv ? (DataGouv) activity.getDataGouv() : null;
					panelDataGouv = new DataGouvProperties(this, dataGouv, activity.getUrl());
				}
				panelDefinition.setWidget(panelDataGouv);
				break;

			default:
				break;
			}
		}
	}

	public void refreshGeneratedUrl(String generatedUrl, DataGouv dataGouv) {
		activity.setDataGouv(dataGouv);
		txtUrl.setText(generatedUrl);
		refresh();
	}

	@Override
	public boolean isValid() {
		int typeServiceId = Integer.parseInt(lstTypeService.getValue(lstTypeService.getSelectedIndex()));
		TypeOpenData typeService = TypeOpenData.valueOf(typeServiceId);

		switch (typeService) {
		case DATA_GOUV:
			return panelDataGouv.isValid();
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
			activity.setDataGouv(panelDataGouv.getDataGouv());
			break;

		default:
			break;
		}

		return activity;
	}
}
