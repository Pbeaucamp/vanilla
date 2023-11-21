package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanillahub.core.beans.activities.GeojsonActivity;
import bpm.vanillahub.core.beans.activities.GeojsonActivity.TypeGeoloc;
import bpm.vanillahub.core.beans.activities.GeojsonActivity.TypeOption;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.workflow.commons.beans.Activity;

public class GeojsonActivityProperties extends PropertiesPanel<Activity> {

	private BoxItem item;
	private GeojsonActivity activity;

	private VariablePropertiesText txtColumnId, txtColumnCoordinate, txtLimit;
	
	private PropertiesText txtSeparator, txtEncoding, txtScore;
	private PropertiesListBox lstGeoloc;
	private VariablePropertiesText txtColumnLat, txtColumnLong;
	private VariablePropertiesText txtColumnNum, txtColumnStreet, txtColumnCity, txtColumnAddress, txtColumnPostalCode;

	public GeojsonActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, GeojsonActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedTypeIndex = -1;
		for (TypeOption type : TypeOption.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeOption() == type) {
				selectedTypeIndex = i;
			}
			i++;
		}

		PropertiesListBox lstOptions = addList(Labels.lblCnst.SelectOption(), actions, WidgetWidth.SMALL_PX, changeTypeOptionSource, null);
		if (selectedTypeIndex != -1) {
			lstOptions.setSelectedIndex(selectedTypeIndex);
		}

		//Part GEOJSON
		txtColumnId = addVariableText(Labels.lblCnst.ColumnIdLeaveEmptyForDefault(), activity.getColumnIdVS(), WidgetWidth.PCT, null);
		txtColumnCoordinate = addVariableText(Labels.lblCnst.ColumnCoordinateLeaveEmptyForDefault(), activity.getColumnGeojsonCoordinateVS(), WidgetWidth.PCT, null);
		txtLimit = addVariableText(Labels.lblCnst.LimitLeaveEmptyForNoLimit(), activity.getLimitVS(), WidgetWidth.PCT, null);


		//Part GEOLOC
		txtSeparator = addText(Labels.lblCnst.Separator(), activity.getSeparator(), WidgetWidth.PCT, false);
		txtEncoding = addText(Labels.lblCnst.Encoding(), activity.getEncoding(), WidgetWidth.PCT, false);

		actions = new ArrayList<ListItem>();
		i = 0;
		selectedTypeIndex = -1;
		for (TypeGeoloc type : TypeGeoloc.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeGeoloc() == type) {
				selectedTypeIndex = i;
			}
			i++;
		}

		lstGeoloc = addList(Labels.lblCnst.SelectTypeDataset(), actions, WidgetWidth.SMALL_PX, changeTypeGeolocSource, null);
		if (selectedTypeIndex != -1) {
			lstGeoloc.setSelectedIndex(selectedTypeIndex);
		}

		txtColumnLat = addVariableText(Labels.lblCnst.ColumnLatitude(), activity.getColumnLatVS(), WidgetWidth.PCT, null);
		txtColumnLong = addVariableText(Labels.lblCnst.ColumnLongitude(), activity.getColumnLongVS(), WidgetWidth.PCT, null);
		
		txtColumnNum = addVariableText(Labels.lblCnst.ColumnNum(), activity.getColumnNumVS(), WidgetWidth.PCT, null);
		txtColumnStreet = addVariableText(Labels.lblCnst.ColumnStreet(), activity.getColumnStreetVS(), WidgetWidth.PCT, null);
		txtColumnCity = addVariableText(Labels.lblCnst.ColumnCity(), activity.getColumnCityVS(), WidgetWidth.PCT, null);
		txtColumnAddress = addVariableText(Labels.lblCnst.ColumnAddress(), activity.getColumnAddressVS(), WidgetWidth.PCT, null);
		txtColumnPostalCode = addVariableText(Labels.lblCnst.ColumnPostalCode(), activity.getColumnPostalCodeVS(), WidgetWidth.PCT, null);
		txtScore = addText(Labels.lblCnst.ScoreBetweenZeroAndHundred(), String.valueOf(activity.getScore()), WidgetWidth.PCT, false);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
		
		updateUi(activity.getTypeOption(), activity.getTypeGeoloc());
	}

	private ChangeHandler changeTypeOptionSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeOption(TypeOption.valueOf(type));
			updateUi(activity.getTypeOption(), activity.getTypeGeoloc());
		}
	};

	private ChangeHandler changeTypeGeolocSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeGeoloc(TypeGeoloc.valueOf(type));
			updateUi(activity.getTypeOption(), activity.getTypeGeoloc());
		}
	};

	private void updateUi(TypeOption typeOption, TypeGeoloc typeGeoloc) {
		boolean isGeojson = typeOption == TypeOption.GEOJSON;
		boolean isColumnLatLong = typeGeoloc == TypeGeoloc.COLUMN_LAT_AND_LONG;
		
		txtColumnId.setVisible(isGeojson);
		txtColumnCoordinate.setVisible(isGeojson);
		txtLimit.setVisible(isGeojson);
		
		lstGeoloc.setVisible(!isGeojson);
		txtSeparator.setVisible(!isGeojson);
		txtEncoding.setVisible(!isGeojson);
		
		txtColumnLat.setVisible(!isGeojson && isColumnLatLong);
		txtColumnLong.setVisible(!isGeojson && isColumnLatLong);

		txtColumnNum.setVisible(!isGeojson && !isColumnLatLong);
		txtColumnStreet.setVisible(!isGeojson && !isColumnLatLong);
		txtColumnCity.setVisible(!isGeojson && !isColumnLatLong);
		txtColumnAddress.setVisible(!isGeojson && !isColumnLatLong);
		txtColumnPostalCode.setVisible(!isGeojson && !isColumnLatLong);
		txtScore.setVisible(!isGeojson && !isColumnLatLong);
	}

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
