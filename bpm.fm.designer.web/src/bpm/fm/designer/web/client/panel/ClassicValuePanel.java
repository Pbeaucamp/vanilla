package bpm.fm.designer.web.client.panel;

import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.dialog.ColumnTableSelectionDialog;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ClassicValuePanel extends ValuePanel {

	private static ClassicValuePanelUiBinder uiBinder = GWT.create(ClassicValuePanelUiBinder.class);

	interface ClassicValuePanelUiBinder extends UiBinder<Widget, ClassicValuePanel> {
	}
	
	@UiField
	TextBox txtTable, txtColumnDate, txtColumnValue, txtObjTable, 
	txtObjColumnValue, txtObjColumnMin, txtObjColumnMax, txtObjColumnDate, txtTolerance;
	
	@UiField
	Button btnTableBrowse, btnColumnValueBrowse, btnColumnDateBrowse, btnObjTableBrowse, 
	btnObjColumnValueBrowse, btnObjColumnMinBrowse, btnObjColumnMaxBrowse, btnObjColumnDateBrowse, btnBrowseTolerance;	

	@UiField
	ListBox lstDatasources, lstPeriodicity, lstAggregator, lstDirection;

	@UiField
	CaptionPanel gridPanel;
	
	private DataGrid<FactTableAxis> dataGrid;
	private ListDataProvider<FactTableAxis> dataProvider;

	private SingleSelectionModel<FactTableAxis> selectionModel;

	private Metric metric;

	public ClassicValuePanel(Metric metric) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.metric = metric;
		
		createDatagrid();
		
		btnTableBrowse.addClickHandler(columnTableHandler);
		btnColumnValueBrowse.addClickHandler(columnTableHandler);
		btnColumnDateBrowse.addClickHandler(columnTableHandler);
		
		btnObjTableBrowse.addClickHandler(columnTableHandler);
		btnObjColumnValueBrowse.addClickHandler(columnTableHandler);
		btnObjColumnMinBrowse.addClickHandler(columnTableHandler);
		btnObjColumnMaxBrowse.addClickHandler(columnTableHandler);
		btnObjColumnDateBrowse.addClickHandler(columnTableHandler);
		btnBrowseTolerance.addClickHandler(columnTableHandler);
		
		gridPanel.add(dataGrid);
	}
	
	private ClickHandler columnTableHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			int datasourceId = Integer.parseInt(lstDatasources.getValue(lstDatasources.getSelectedIndex()));
			
			TextBox txtTofill = findTxtToFill(event);
			
			boolean tableOnly = false;
			ColumnTableSelectionDialog dial = null;
			if(event.getSource() == btnTableBrowse || event.getSource() == btnObjTableBrowse) {
				tableOnly = true;
				dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly);
				
			}
			else if(event.getSource() == btnColumnDateBrowse || event.getSource() == btnColumnValueBrowse){
				dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly, txtTable.getText());
			}
			else if(event.getSource() == btnObjColumnDateBrowse || event.getSource() == btnObjColumnMaxBrowse || 
					event.getSource() == btnObjColumnMinBrowse || event.getSource() == btnObjColumnValueBrowse
					|| event.getSource() == btnBrowseTolerance){
				dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly, txtObjTable.getText());
			}

			dial.center();
		}
	};
	
	private TextBox findTxtToFill(ClickEvent event) {
		if(event.getSource() == btnColumnDateBrowse) {
			return txtColumnDate;
		}
		else if(event.getSource() == btnColumnValueBrowse) {
			return txtColumnValue;
		}
		else if(event.getSource() == btnObjColumnDateBrowse) {
			return txtObjColumnDate;
		}
		else if(event.getSource() == btnObjColumnMaxBrowse) {
			return txtObjColumnMax;
		}
		else if(event.getSource() == btnObjColumnMinBrowse) {
			return txtObjColumnMin;
		}
		else if(event.getSource() == btnObjColumnValueBrowse) {
			return txtObjColumnValue;
		}
		else if(event.getSource() == btnObjTableBrowse) {
			return txtObjTable;
		}
		else if(event.getSource() == btnTableBrowse) {
			return txtTable;
		}
		
		return null;
	}

	private void createDatagrid() {
		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<FactTableAxis>(15, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		TextCell cell = new TextCell();

		Column<FactTableAxis, String> colName = new Column<FactTableAxis, String>(cell) {
			@Override
			public String getValue(FactTableAxis object) {
				return object.getAxis().getName();
			}
		};

		Column<FactTableAxis, String> colColumnId = new Column<FactTableAxis, String>(cell) {
			@Override
			public String getValue(FactTableAxis object) {
				return object.getColumnId();
			}
		};

		Column<FactTableAxis, String> colColumnObjId = new Column<FactTableAxis, String>(cell) {
			@Override
			public String getValue(FactTableAxis object) {
				return object.getObjectiveColumnId();
			}
		};

		dataGrid.addColumn(colName, Messages.lbl.axis());
		dataGrid.addColumn(colColumnId, Messages.lbl.columnMetricAxis());
		dataGrid.addColumn(colColumnObjId, Messages.lbl.columnObjectiveAxis());

		dataGrid.setEmptyTableWidget(new Label(Messages.lbl.NoAxis()));

		dataProvider = new ListDataProvider<FactTableAxis>();
		dataProvider.addDataDisplay(dataGrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(dataGrid);

		selectionModel = new SingleSelectionModel<FactTableAxis>();
		dataGrid.setSelectionModel(selectionModel);
	}

	@Override
	public void fillData() {
		CommonService.Connect.getInstance().getDatasources(new AsyncCallback<List<Datasource>>() {
			@Override
			public void onSuccess(List<Datasource> result) {
				
				lstDatasources.clear();
				lstPeriodicity.clear();

				

				txtColumnDate.setText(((FactTable)metric.getFactTable()).getDateColumn());
				txtColumnValue.setText(((FactTable)metric.getFactTable()).getValueColumn());
				txtTable.setText(((FactTable)metric.getFactTable()).getTableName());

				FactTableObjectives objectives = ((FactTable)metric.getFactTable()).getObjectives();
				if(objectives != null) {
					txtObjTable.setText(objectives.getTableName());
					txtObjColumnValue.setText(objectives.getObjectiveColumn());
					txtObjColumnMax.setText(objectives.getMaxColumn());
					txtObjColumnMin.setText(objectives.getMinColumn());
					txtObjColumnDate.setText(objectives.getDateColumn());
					txtTolerance.setText(objectives.getTolerance());
				}

				int i = 0;
				for (Datasource datasource : result) {
					lstDatasources.addItem(datasource.getName(), String.valueOf(datasource.getId()));
					if (((FactTable)metric.getFactTable()).getDatasourceId() == datasource.getId()) {
						lstDatasources.setSelectedIndex(i);
					}
					i++;
				}
				
				i = 0;
				for (String period : FactTable.PERIODICITIES) {
					lstPeriodicity.addItem(period, period);
					if (((FactTable)metric.getFactTable()).getPeriodicity().equals(period)) {
						lstPeriodicity.setSelectedIndex(i);
					}
					i++;
				}

				refresh(metric);
			}

			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.GetDatasourceError(), caught.getMessage(), caught);
				dial.center();
			}
		});
		
		for(int i = 0 ; i < Metric.AGGREGATORS.size() ; i++) {
			lstAggregator.addItem(Metric.AGGREGATORS.get(i), Metric.AGGREGATORS.get(i));
			if(metric.getOperator().equals(Metric.AGGREGATORS.get(i))) {
				lstAggregator.setSelectedIndex(i);
			}
		}
		
		for(int i = 0 ; i < Metric.DIRECTIONS.size() ; i++) {
			if(i == Metric.DIRECTION_BOTTOM) {
				lstDirection.addItem("Desc", Metric.DIRECTIONS.get(i));
			}
			else if(i == Metric.DIRECTION_TOP) {
				lstDirection.addItem("Asc", Metric.DIRECTIONS.get(i));
			}
			else {
				lstDirection.addItem("Middle", Metric.DIRECTIONS.get(i));
			}
			
			if(metric.getDirection() != null) {
				if(metric.getDirection().equals(Metric.DIRECTIONS.get(i))) {
					lstDirection.setSelectedIndex(i);
				}
			}
		}
	}

	@Override
	public Metric getMetric() {
		metric.setOperator(lstAggregator.getValue(lstAggregator.getSelectedIndex()));
		metric.setDirection(lstDirection.getValue(lstDirection.getSelectedIndex()));
		
		int datasourceId = Integer.parseInt(lstDatasources.getValue(lstDatasources.getSelectedIndex()));
		String periodicity = lstPeriodicity.getValue(lstPeriodicity.getSelectedIndex());
		
		((FactTable)metric.getFactTable()).setDatasourceId(datasourceId);
		((FactTable)metric.getFactTable()).setDateColumn(txtColumnDate.getText());
		((FactTable)metric.getFactTable()).setPeriodicity(periodicity);
		((FactTable)metric.getFactTable()).setTableName(txtTable.getText());
		((FactTable)metric.getFactTable()).setValueColumn(txtColumnValue.getText());
		
		((FactTable)metric.getFactTable()).getObjectives().setDateColumn(txtObjColumnDate.getText());
		((FactTable)metric.getFactTable()).getObjectives().setMaxColumn(txtObjColumnMax.getText());
		((FactTable)metric.getFactTable()).getObjectives().setMinColumn(txtObjColumnMin.getText());
		((FactTable)metric.getFactTable()).getObjectives().setObjectiveColumn(txtObjColumnValue.getText());
		((FactTable)metric.getFactTable()).getObjectives().setTableName(txtObjTable.getText());
		((FactTable)metric.getFactTable()).getObjectives().setTolerance(txtTolerance.getText());
		
		return metric;
	}

	@Override
	public void refresh(Metric metric) {
		this.metric = metric;
		if (metric != null) {
			dataProvider.setList(((FactTable)metric.getFactTable()).getFactTableAxis());
		}
		
	}

	public FactTableAxis getSelectedAxis() {
		return selectionModel.getSelectedObject();
	}
}
