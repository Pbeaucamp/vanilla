package bpm.fm.designer.web.client.dialog;

import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricMap;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MapDialog extends AbstractDialogBox {

	private static MapDialogUiBinder uiBinder = GWT
			.create(MapDialogUiBinder.class);
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextBox txtName, txtTable, txtColumnZone, txtColumnLatitude, txtColumnLongitude;
	
	@UiField 
	TextArea txtDesc;
	
	@UiField
	ListBox lstDatasource, lstLevel;
	
	@UiField
	Button btnTableBrowse, btnColumnZoneBrowse, btnColumnLatitudeBrowse, btnColumnLongitudeBrowse;
	
	private MetricMap map;
	
	private List<Datasource> datasources;

	private ClickHandler columnTableHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			int datasourceId = Integer.parseInt(lstDatasource.getValue(lstDatasource.getSelectedIndex()));
			
			TextBox txtTofill = findTxtToFill(event);
			
			boolean tableOnly = false;
			
			if(event.getSource() == btnTableBrowse) {
				tableOnly = true;
			}
			
			ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly);
			dial.center();
		}
	};

	private Metric metric;
	
	interface MapDialogUiBinder extends UiBinder<Widget, MapDialog> {
	}

	public MapDialog(Metric metric, MetricMap maps) {
		super(Messages.lbl.addEditMap(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.map = maps;
		this.metric = metric;
		
		btnColumnLatitudeBrowse.addClickHandler(columnTableHandler);
		btnColumnLongitudeBrowse.addClickHandler(columnTableHandler);
		btnColumnZoneBrowse.addClickHandler(columnTableHandler);
		btnTableBrowse.addClickHandler(columnTableHandler);
		
		CommonService.Connect.getInstance().getDatasources(new AsyncCallback<List<Datasource>>() {
			
			@Override
			public void onSuccess(List<Datasource> result) {
				datasources = result;
				for(Datasource ds : datasources) {
					lstDatasource.addItem(ds.getName(), String.valueOf(ds.getId()));
				}
				if(map != null) {
					fillData();
				}
				
				createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.ProblemLoadDatasource(), caught.getMessage(), caught);
				dial.center();
			}
		});
		
		if(metric.getFactTable() instanceof FactTable) {
			int i = 0;
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				for(Level lvl : axis.getAxis().getChildren()) {
					lstLevel.addItem(lvl.getParent().getName() + " - " + lvl.getName(), lvl.getId() + "");
					if(map != null && map.getLevelId() == lvl.getId()) {
						lstLevel.setSelectedIndex(i);
					}
					i++;
				}
			}
		}
 	}
	
	protected TextBox findTxtToFill(ClickEvent event) {
		if(event.getSource() == btnColumnLatitudeBrowse) {
			return txtColumnLatitude;
		}
		else if(event.getSource() == btnColumnLongitudeBrowse) {
			return txtColumnLongitude;
		}
		else if(event.getSource() == btnColumnZoneBrowse) {
			return txtColumnZone;
		}
		else if(event.getSource() == btnTableBrowse) {
			return txtTable;
		}
		
		return null;
	}

	private void fillData() {
		txtName.setText(map.getName());
		txtTable.setText(map.getTableName());
		txtColumnLatitude.setText(map.getColumnLatitude());
		txtColumnLongitude.setText(map.getColumnLongitude());
		txtColumnZone.setText(map.getColumnZone());
		txtDesc.setText(map.getDesc());
		
		for(int i = 0; i < lstDatasource.getItemCount() ; i++) {
			int datasourceId = Integer.parseInt(lstDatasource.getValue(i));
			if(datasourceId == map.getDatasourceId()) {
				lstDatasource.setSelectedIndex(i);
				break;
			}
		}
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			if(map == null) {
				map = new MetricMap();
			}
			
			map.setMetricId(metric.getId());
			
			map.setName(txtName.getText());
			map.setTableName(txtTable.getText());
			map.setDesc(txtDesc.getText());
			map.setColumnLatitude(txtColumnLatitude.getText());
			map.setColumnLongitude(txtColumnLongitude.getText());
			map.setColumnZone(txtColumnZone.getText());
			
			int levelId = Integer.parseInt(lstLevel.getValue(lstLevel.getSelectedIndex()));
			map.setLevelId(levelId);
			
			int datasourceId = Integer.parseInt(lstDatasource.getValue(lstDatasource.getSelectedIndex()));
			map.setDatasourceId(datasourceId);
			
			for(Datasource datasource : datasources) {
				if(datasource.getId() == datasourceId) {
					map.setDatasource(datasource);
					break;
				}
			}
			
			MetricService.Connection.getInstance().addMetricMap(map, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Void result) {
					MapDialog.this.hide();
				}
			});
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			MapDialog.this.hide();
		}
	};

}
