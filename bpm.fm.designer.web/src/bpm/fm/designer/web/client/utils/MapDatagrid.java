package bpm.fm.designer.web.client.utils;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricMap;
import bpm.fm.designer.web.client.Messages;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class MapDatagrid extends DataGrid<MetricMap>{

	private ListDataProvider<MetricMap> dataprovider;
	private SingleSelectionModel<MetricMap> selectionModel;
	
	public MapDatagrid() {
		super(20);
		
		this.setWidth("100%");
		this.setHeight("100%");
		
		TextCell cell = new TextCell();
		
		Column<MetricMap, String> colName = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getName();
			}
		};
		Column<MetricMap, String> colDesc = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getDesc();
			}
		};
		Column<MetricMap, String> colDatasource = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getDatasource().getName();
			}
		};
		Column<MetricMap, String> colColZone = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getColumnZone();
			}
		};
		Column<MetricMap, String> colColLat = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getColumnLatitude();
			}
		};
		Column<MetricMap, String> colColLong = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getColumnLongitude();
			}
		};
		
		Column<MetricMap, String> colColLvl = new Column<MetricMap, String>(cell) {
			@Override
			public String getValue(MetricMap object) {
				return object.getLevel().getName();
			}
		};
		
		this.addColumn(colName, Messages.lbl.name());
		this.addColumn(colDesc, Messages.lbl.description());
		this.addColumn(colDatasource, Messages.lbl.datasource());
		this.addColumn(colColZone, "Zone");
		this.addColumn(colColLat, "Latitude");
		this.addColumn(colColLong, "Longitude");
		this.addColumn(colColLvl, Messages.lbl.name());
		
		this.setEmptyTableWidget(new Label(Messages.lbl.noMaps()));
		
		
		dataprovider = new ListDataProvider<MetricMap>();
		dataprovider.addDataDisplay(this);
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(this);
		
		selectionModel = new SingleSelectionModel<MetricMap>();
		this.setSelectionModel(selectionModel);
	}

	public void setMetric(Metric metric) {
		dataprovider.setList(metric.getMaps());
	}

	public MetricMap getSelection() {
		return selectionModel.getSelectedObject();
	}
	
}
