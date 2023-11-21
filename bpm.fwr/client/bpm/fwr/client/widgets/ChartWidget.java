package bpm.fwr.client.widgets;

import java.util.List;

import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.ChartUtils;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.wizard.chart.ChartCreationWizard;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ChartWidget extends ReportWidget implements HasMouseDownHandlers{
	
	private IChart chartComponent;
	private ReportSheet reportSheetParent;
	private GridWidget gridWidget;
	private Image image;
	
	public ChartWidget(ReportSheet reportSheetParent, IChart chartComponent, ImageResource resource, 
			int width, GridWidget gridWidget) {
		super(reportSheetParent, WidgetType.CHART, width);
		this.setChartComponent(chartComponent);
		this.reportSheetParent = reportSheetParent;
		this.gridWidget = gridWidget;
		
		image = new Image(resource);
		image.addDoubleClickHandler(doubleClickHandler);
		this.add(image);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
	
	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {
		
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			List<DataSet> dsAvailables = reportSheetParent.getAvailableDatasets();
			
			ChartCreationWizard wiz = new ChartCreationWizard(reportSheetParent, chartComponent, dsAvailables);
			wiz.addFinishListener(finishListener);
			wiz.center();
		}
	};
	
	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if(result instanceof VanillaChartComponent){
				VanillaChartComponent chart = (VanillaChartComponent)result;
				setChartComponent(chart);
				image.setResource(ChartUtils.getImageForChart(chart.getChartType().getType(), chart.getOptions()));
			}
			else if(result instanceof ChartComponent) {
				ChartComponent chart = (ChartComponent)result;
				setChartComponent(chart);
				image.setResource(ChartUtils.getImageForChart(chart.getChartType()));
			}
			
		}
	};

	private void setChartComponent(IChart chartComponent) {
		this.chartComponent = chartComponent;
	}

	public IChart getChartComponent(int row, int col) {
		chartComponent.setX(col);
		chartComponent.setY(row);
		return chartComponent;
	}

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
