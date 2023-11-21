package bpm.freematrix.reborn.web.client.main.home.application.details;

import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.images.Images;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DetailsApplications extends Composite {

	private static DetailsApplicationsUiBinder uiBinder = GWT
			.create(DetailsApplicationsUiBinder.class);
	
	

	interface DetailsApplicationsUiBinder extends
			UiBinder<Widget, DetailsApplications> {
	}

	
	@UiField HTMLPanel gridPanel;
	
	private ListDataProvider<MetricValue> dataProvider;
	private DataGrid<MetricValue> datagrid;
	
	public DetailsApplications(Date date, Axis parent, Metric metric) {
		initWidget(uiBinder.createAndBindUi(this));
		
		createDatagrid();
		
		getValuesByDateAndAxis(date, parent, metric);
	}

	private void createDatagrid() {
		DataGrid.Resources resources = new CustomResources();
		datagrid = new DataGrid<MetricValue>(99999, resources);
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
		
		Column<MetricValue, String> colAxis = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				return object.getAxis().toString();
			}
		};
		
		Column<MetricValue, String> colValue = new Column<MetricValue, String>(cell) {
			@Override
			public String getValue(MetricValue object) {
				return object.getValue() + "";
			}
		};
			
		datagrid.addColumn(colAxis, LabelConstants.lblCnst.axis());
		datagrid.addColumn(colValue, LabelConstants.lblCnst.Value());
		datagrid.addColumn(colHealth, LabelConstants.lblCnst.Health());
		
		datagrid.setColumnWidth(colAxis, "45%");
		datagrid.setColumnWidth(colValue, "35%");
		datagrid.setColumnWidth(colHealth, "250px");
		
		dataProvider = new ListDataProvider<MetricValue>();
		dataProvider.addDataDisplay(datagrid);
		
		datagrid.setEmptyTableWidget(new Label());
		
		gridPanel.add(datagrid);
	}

	private void getValuesByDateAndAxis(Date date,Axis axis, final Metric metric){
		WaitDialog.showWaitPart(true);
		MetricService.Connection.getInstance().getValuesByDateAxisMetric(date, axis.getChildren().get(axis.getChildren().size() - 1), metric.getId(), FreeMatrixHeader.getInstance().getSelectedGroup(), new AsyncCallback<List<MetricValue>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<MetricValue> result) {
				dataProvider.setList(result);
				WaitDialog.showWaitPart(false);
			}
		});
	}
			
}
