package bpm.fm.designer.web.client.utils;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.designer.web.client.Messages;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ActionDatagrid extends DataGrid<MetricAction> {
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
	
	private ListDataProvider<MetricAction> dataprovider;
	private SingleSelectionModel<MetricAction> selectionModel;
	
	public ActionDatagrid() {
		super(20);
		
		this.setWidth("100%");
		this.setHeight("100%");
		
		TextCell cell = new TextCell();
		
		Column<MetricAction, String> colName = new Column<MetricAction, String>(cell) {
			@Override
			public String getValue(MetricAction object) {
				return object.getName();
			}
		};
		Column<MetricAction, String> colDesc = new Column<MetricAction, String>(cell) {
			@Override
			public String getValue(MetricAction object) {
				return object.getDescription();
			}
		};
		Column<MetricAction, String> colStartDate = new Column<MetricAction, String>(cell) {
			@Override
			public String getValue(MetricAction object) {
				return dateFormat.format(object.getStartDate());
			}
		};
		Column<MetricAction, String> colEndDate = new Column<MetricAction, String>(cell) {
			@Override
			public String getValue(MetricAction object) {
				return dateFormat.format(object.getEndDate());
			}
		};
		Column<MetricAction, String> colFormula = new Column<MetricAction, String>(cell) {
			@Override
			public String getValue(MetricAction object) {
				return object.getFormula();
			}
		};
		
		this.addColumn(colName, Messages.lbl.name());
		this.addColumn(colDesc, Messages.lbl.description());
		this.addColumn(colStartDate, Messages.lbl.startDate());
		this.addColumn(colEndDate, Messages.lbl.endDate());
		this.addColumn(colFormula, Messages.lbl.formula());
		
		this.setEmptyTableWidget(new Label(Messages.lbl.noActions()));
		
		
		dataprovider = new ListDataProvider<MetricAction>();
		dataprovider.addDataDisplay(this);
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(this);
		
		selectionModel = new SingleSelectionModel<MetricAction>();
		this.setSelectionModel(selectionModel);
	}

	public void setMetric(Metric metric) {
		dataprovider.setList(metric.getMetricActions());
	}

	public MetricAction getSelection() {
		return selectionModel.getSelectedObject();
	}
	
}
