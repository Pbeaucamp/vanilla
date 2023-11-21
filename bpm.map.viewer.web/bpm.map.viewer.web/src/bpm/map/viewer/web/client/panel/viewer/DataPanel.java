package bpm.map.viewer.web.client.panel.viewer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.images.Images;
import bpm.map.viewer.web.client.panel.ViewerPanel;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DataPanel extends Composite{
	private static DataPanelUiBinder uiBinder = GWT
			.create(DataPanelUiBinder.class);
	
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	interface DataPanelUiBinder extends
			UiBinder<Widget, DataPanel> {
	}
	
	interface MyStyle extends CssResource {
		String pager();
	}

	@UiField
	MyStyle style;

	
	@UiField
	HTMLPanel gridPanel;
	
	@UiField
	SimplePanel panelPager;
	
	@UiField
	Label lblData;
	
	@UiField
	ListBox lstData;
	
	private ListDataProvider<MetricValue> dataProvider;
	private DataGrid<MetricValue> datagrid;
	private ListHandler<MetricValue> sortHandler;
	private Metric selectedMetric;
	private List<Metric> metrics;
	private List<MapZoneValue> mapzonevalues = new ArrayList<MapZoneValue>();
	private List<ComplexObjectViewer> viewers;
	private ViewerPanel viewer;
	private List<Level> levels;
	
	private int levelIndex;
	
	public DataPanel(ViewerPanel viewer, List<Level> levels, List<Metric> metrics, List<ComplexObjectViewer> viewers) {
		initWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;
		this.metrics = metrics;
		this.viewers = viewers;
		this.levels = levels;
		
		createDatagrid();
		
		for(Metric met: metrics){
			lstData.addItem(met.getName());
		}
		
		lstData.setItemSelected(0, true);
		this.selectedMetric = metrics.get(lstData.getSelectedIndex());
		
		this.levelIndex = levels.get(0).getParent().getChildren().indexOf(levels.get(0));
		
		loadData();
	}

	private void createDatagrid() {
		datagrid = new DataGrid<MetricValue>(50);
		datagrid.setHeight("100%");
		datagrid.setWidth("100%");
		
		TextCell cell = new TextCell();
		
		Column<MetricValue, ImageResource> colHealth = new Column<MetricValue, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(MetricValue object) {
				if(object.getHealth() < 0) {
					return Images.INSTANCE.thumb_down();
				}
				else if(object.getHealth() == 0) {
					return Images.INSTANCE.thumb_equal();
				}
				else {
					return Images.INSTANCE.thumb_up();
				}
			}
		};
		colHealth.setSortable(true);
		
		Column<MetricValue, String> colAxis = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				//return object.getAxis().toString();
				return object.getAxis().get(levelIndex).getLabel().replace("'", "");
			}
		};
		colAxis.setSortable(true);
		
		
		Column<MetricValue, String> colValue = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				return object.getValue() + "";
			}
		};
		colValue.setSortable(true);
			
		datagrid.addColumn(colAxis, lblCnst.AxisName());
		datagrid.addColumn(colValue, "Valeur");
		datagrid.addColumn(colHealth, "Tendance");
		
		datagrid.setColumnWidth(colAxis, "45%");
		datagrid.setColumnWidth(colValue, "35%");
		datagrid.setColumnWidth(colHealth, "30%");
		
		dataProvider = new ListDataProvider<MetricValue>();
		dataProvider.addDataDisplay(datagrid);
		
		datagrid.setEmptyTableWidget(new Label());
		
		sortHandler = new ListHandler<MetricValue>(new ArrayList<MetricValue>());
		sortHandler.setComparator(colAxis, new Comparator<MetricValue>() {

			@Override
			public int compare(MetricValue m1, MetricValue m2) {
				return m1.getAxis().get(levelIndex).getLabel().replace("'", "").compareToIgnoreCase(m2.getAxis().get(levelIndex).getLabel().replace("'", ""));
			}
		});

		sortHandler.setComparator(colValue, new Comparator<MetricValue>() {

			@Override
			public int compare(MetricValue m1, MetricValue m2) {
				return (int) Math.round(m1.getValue() - m2.getValue());
			}
		});

		sortHandler.setComparator(colHealth, new Comparator<MetricValue>() {

			@Override
			public int compare(MetricValue m1, MetricValue m2) {
				return m1.getHealth() - m2.getHealth();
			}
		});
		
		datagrid.addColumnSortHandler(sortHandler);
		
		
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(datagrid);
		
		panelPager.setWidget(pager);
		
		gridPanel.add(datagrid);
	}

	public void loadData(){
		
		
		mapzonevalues.clear();
		for(ComplexObjectViewer cov : viewers){
			if(cov.getMetric().getId() == selectedMetric.getId()){
				mapzonevalues.addAll(cov.getMapvalues());
			}
		}
		
		List<MetricValue> result = new ArrayList<MetricValue>();
		if(viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
			for(MapZoneValue mzv : mapzonevalues){
				result.add(mzv.getValues().get(0));
			}
		} else {
			for(MapZoneValue mzv : mapzonevalues){
				MetricValue metval = new MetricValue();
				metval.setAxis(mzv.getValues().get(0).getAxis());
				metval.setMetric(mzv.getValues().get(0).getMetric());
				metval.setHealth(mzv.getValues().get(0).getHealth());
				
				metval.setValue(new BigDecimal(mzv.getValues().get(mzv.getValues().size() -1).getValue()+"").subtract(new BigDecimal(mzv.getValues().get(0).getValue()+"")).doubleValue());
				result.add(metval);
			}
		}
		if(result != null){
			Collections.sort(result, new Comparator<MetricValue>() {

				@Override
				public int compare(MetricValue m1, MetricValue m2) {
					return m1.getAxis().get(levelIndex).getLabel().replace("'", "").compareToIgnoreCase(m2.getAxis().get(levelIndex).getLabel().replace("'", ""));
				}
			});
		}
		
		dataProvider.setList(result);
		sortHandler.setList(dataProvider.getList());
		
	}
	
	@UiHandler("lstData")
	public void onDataChange(ChangeEvent event) {
		selectedMetric = metrics.get(lstData.getSelectedIndex());
		loadData();
	}
	
}
