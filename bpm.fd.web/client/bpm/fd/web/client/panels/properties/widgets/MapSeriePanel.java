package bpm.fd.web.client.panels.properties.widgets;

import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapSeriePanel extends Composite {

	private static MapSeriePanelUiBinder uiBinder = GWT.create(MapSeriePanelUiBinder.class);

	interface MapSeriePanelUiBinder extends UiBinder<Widget, MapSeriePanel> {
	}
	
	@UiField
	LabelTextBox serieName;
	
	@UiField
	CustomCheckbox displayLayer;
	
	@UiField
	HTMLPanel panelMarkers, panelZones;
	
	@UiField
	LabelValueTextBox minMarker, maxMarker;

	@UiField
	ColorPanel minColor, maxColor;
	
	private VanillaMapDataSerie currentSerie;

	public MapSeriePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		serieName.setEnabled(false);
		
	}

	public void setSerie(VanillaMapDataSerie serie) {
		
		currentSerie = serie;
		
		serieName.setText(serie.getName());
		displayLayer.setValue(serie.isDisplay());
		
		if(serie.getType().equals(VanillaMapDataSerie.MARKER)) {
			panelZones.getElement().getStyle().setDisplay(Display.NONE);
			panelMarkers.getElement().getStyle().setDisplay(Display.BLOCK);
			
			minMarker.setText(serie.getMinMarkerSize());
			maxMarker.setText(serie.getMaxMarkerSize());
		}
		else {
			panelZones.getElement().getStyle().setDisplay(Display.BLOCK);
			panelMarkers.getElement().getStyle().setDisplay(Display.NONE);
			
			minColor.setColor(serie.getMinColor());
			maxColor.setColor(serie.getMaxColor());
		}
		
	}
	
	@UiHandler("btnApply")
	public void onApply(ClickEvent event) {
		currentSerie.setDisplay(displayLayer.getValue());
		currentSerie.setMinMarkerSize(minMarker.getValue());
		currentSerie.setMaxMarkerSize(maxMarker.getValue());
		currentSerie.setMinColor(minColor.getColor());
		currentSerie.setMaxColor(maxColor.getColor());
	}

	@UiHandler("btnCancel")
	public void onCancel(ClickEvent event) {
		setSerie(currentSerie);
	}
	
}
