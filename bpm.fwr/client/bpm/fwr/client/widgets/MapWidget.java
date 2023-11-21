package bpm.fwr.client.widgets;

import java.util.List;

import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.wizard.map.VanillaMapsWizard;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

public class MapWidget extends ReportWidget implements HasMouseDownHandlers {

	private VanillaMapComponent mapComponent;
	private ReportSheet reportSheetParent;
	private GridWidget gridWidget;
	
	public MapWidget(ReportSheet reportSheetParent, VanillaMapComponent mapComponent, int width, GridWidget gridWidget) {
		super(reportSheetParent, WidgetType.VANILLA_MAP, width);
		this.setMapComponent(mapComponent);
		this.setReportSheetParent(reportSheetParent);
		this.gridWidget = gridWidget;
		
		Image image = new Image(WysiwygImage.INSTANCE.VanillaMapBig());
		image.addDoubleClickHandler(doubleClickHandler);
		this.add(image);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public void setMapComponent(VanillaMapComponent mapComponent) {
		this.mapComponent = mapComponent;
	}

	public VanillaMapComponent getMapComponent(int row, int col) {
		mapComponent.setX(col);
		mapComponent.setY(row);
		return mapComponent;
	}

	public void setReportSheetParent(ReportSheet reportSheetParent) {
		this.reportSheetParent = reportSheetParent;
	}

	public ReportSheet getReportSheetParent() {
		return reportSheetParent;
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {
		
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			List<DataSet> dsAvailables = reportSheetParent.getAvailableDatasets();
			
			VanillaMapsWizard wiz = new VanillaMapsWizard(reportSheetParent, mapComponent, dsAvailables);
			wiz.addFinishListener(finishListener);
			wiz.center();
		}
	};
	
	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if(result instanceof VanillaMapComponent){
				setMapComponent((VanillaMapComponent)result);
			}			
		}
	};

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	@Override
	public void widgetToTrash(Object widget) {
		if(gridWidget != null){
			gridWidget.widgetToTrash(widget);
		}
		else {
			reportSheetParent.widgetToTrash(widget);
		}
	}
}
