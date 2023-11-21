package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.MapComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class MapOptionsProperties extends CompositeProperties<IComponentOption> {

	private static MapOptionsPropertiesUiBinder uiBinder = GWT.create(MapOptionsPropertiesUiBinder.class);

	interface MapOptionsPropertiesUiBinder extends UiBinder<Widget, MapOptionsProperties> {
	}
	
	@UiField
	CustomCheckbox showLegend, showBaseLayer;
	
	@UiField
	ListBoxWithButton<Item> orientation, layout;
	
	@UiField
	LabelTextBox numberFormat;

	public MapOptionsProperties(MapComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		orientation.setList(getOrientations());
		layout.setList(getLayouts());
		
		showLegend.setValue(component.isShowLegend());
		showBaseLayer.setValue(component.isShowBaseLayer());
		
		numberFormat.setText(component.getNumberFormat());
		
		orientation.setSelectedIndex(MapComponent.orientations.indexOf(component.getLegendOrientation()));
		layout.setSelectedIndex(MapComponent.layouts.indexOf(component.getLegendLayout()));
	}

	@Override
	public void buildProperties(IComponentOption component) {
		MapComponent map = (MapComponent) component;

		Item selectedLayout = layout.getSelectedObject();
		Item selectedOrientation = orientation.getSelectedObject();
		
		map.setLegendLayout(selectedLayout.getValue());
		map.setLegendOrientation(selectedOrientation.getValue());
		map.setShowBaseLayer(showBaseLayer.getValue());
		map.setShowLegend(showLegend.getValue());
		map.setNumberFormat(numberFormat.getText());
	}
	
	private List<Item> getOrientations() {
		List<Item> items = new ArrayList<>();
		for (String orientation : MapComponent.orientations) {
			String name = "";
			switch (orientation) {
			case MapComponent.ORIENTATION_HORIZONTAL:
				name = Labels.lblCnst.Horizontal();
				break;
			case MapComponent.ORIENTATION_VERTICAL:
				name = Labels.lblCnst.Vertical();
				break;

			default:
				break;
			}
			items.add(new Item(name, orientation));
		}
		return items;
	}
	
	private List<Item> getLayouts() {
		List<Item> items = new ArrayList<>();
		for (String layout : MapComponent.layouts) {
			String name = "";
			switch (layout) {
			case MapComponent.LAYOUT_BOTTOM:
				name = Labels.lblCnst.Bottom();
				break;
			case MapComponent.LAYOUT_LEFT:
				name = Labels.lblCnst.Left();
				break;
			case MapComponent.LAYOUT_RIGHT:
				name = Labels.lblCnst.Right();
				break;
			case MapComponent.LAYOUT_TOP:
				name = Labels.lblCnst.Top();
				break;

			default:
				break;
			}
			items.add(new Item(name, layout));
		}
		return items;
	}

	public class Item {
		
		private String name;
		private String value;
		
		public Item(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
