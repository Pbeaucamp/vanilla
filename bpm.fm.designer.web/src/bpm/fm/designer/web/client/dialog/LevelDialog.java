package bpm.fm.designer.web.client.dialog;

import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LevelDialog extends AbstractDialogBox {

	private static LevelDialogUiBinder uiBinder = GWT.create(LevelDialogUiBinder.class);

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextBox txtName, txtTable, txtColumnId, txtColumnName, txtParentColumn, txtGeoColumn;
	
	@UiField
	ListBox lstDatasource, lstDataset;
	
	@UiField
	Button btnTableBrowse, btnColumnIdBrowse, btnColumnNameBrowse, btnColumnParentBrowse, btnColumnGeoBrowse;
	
	private Axis axe;
	private Level level;
	
	private List<Datasource> datasources;
	private List<MapDataSet> datasets;
	
	interface LevelDialogUiBinder extends UiBinder<Widget, LevelDialog> {
	}

	public LevelDialog(final Axis axe, final Level level) {
		super(Messages.lbl.AddEditLevel(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.axe = axe;
		this.level = level;
		
		btnColumnIdBrowse.addClickHandler(columnTableHandler);
		btnColumnNameBrowse.addClickHandler(columnTableHandler);
		btnColumnParentBrowse.addClickHandler(columnTableHandler);
		btnTableBrowse.addClickHandler(columnTableHandler);
		btnColumnGeoBrowse.addClickHandler(columnTableHandler);
		
		/* kevin M */
		datasets = ClientSession.getInstance().getDatasets();
		List<MapVanilla> maps = ClientSession.getInstance().getMaps();
		String mapName = "";
		lstDataset.addItem("Aucun", "0");
		for(MapDataSet ds : datasets) {
			for(MapVanilla map : maps) {
				if(map.getId() == ds.getIdMapVanilla()){
					mapName = map.getName();
					break;
				}
			}
			lstDataset.addItem(mapName +" - " + ds.getName(), String.valueOf(ds.getId()));
		}
		
		CommonService.Connect.getInstance().getDatasources(new AsyncCallback<List<Datasource>>() {
			
			@Override
			public void onSuccess(List<Datasource> result) {
				datasources = result;
				for(Datasource ds : datasources) {
					lstDatasource.addItem(ds.getName(), String.valueOf(ds.getId()));
				}
				if(level != null) {
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
		
		
	}

	private void fillData() {
		txtName.setText(level.getName());
		txtTable.setText(level.getTableName());
		txtColumnId.setText(level.getColumnId());
		txtColumnName.setText(level.getColumnName());
		txtParentColumn.setText(level.getParentColumnId());
		txtGeoColumn.setText(level.getGeoColumnId());
		
		for(int i = 0; i < lstDatasource.getItemCount() ; i++) {
			int datasourceId = Integer.parseInt(lstDatasource.getValue(i));
			if(datasourceId == level.getDatasourceId()) {
				lstDatasource.setSelectedIndex(i);
				break;
			}
		}
		/* kevin */
		if(level.getMapDatasetId() != null){
			for(int i = 0; i < lstDataset.getItemCount() ; i++) {
				int datasetId = Integer.parseInt(lstDataset.getValue(i));
				if(datasetId == level.getMapDatasetId()) {
					lstDataset.setSelectedIndex(i);
					break;
				}
			}
		}
		
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			if(level == null) {
				level = new Level();
				axe.addChild(level);
			}
			
			level.setColumnId(txtColumnId.getText());
			level.setColumnName(txtColumnName.getText());
			level.setName(txtName.getText());
			level.setTableName(txtTable.getText());
			level.setParentColumnId(txtParentColumn.getText());
			level.setGeoColumnId(txtGeoColumn.getText());
			int datasourceId = Integer.parseInt(lstDatasource.getValue(lstDatasource.getSelectedIndex()));
			level.setDatasourceId(datasourceId);
			/* kevin */
			int datasetId = Integer.parseInt(lstDataset.getValue(lstDataset.getSelectedIndex()));
			level.setMapDatasetId(datasetId);
			
			for(Datasource datasource : datasources) {
				if(datasource.getId() == datasourceId) {
					level.setDatasource(datasource);
					break;
				}
			}
			
			LevelDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			LevelDialog.this.hide();
		}
	};
	
	private ClickHandler columnTableHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			int datasourceId = Integer.parseInt(lstDatasource.getValue(lstDatasource.getSelectedIndex()));
			
			TextBox txtTofill = findTxtToFill(event);
			
			boolean tableOnly = false;
			
			if(event.getSource() == btnTableBrowse) {
				tableOnly = true;
			
				ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly);
				dial.center();
			}
			else {
				if(!txtTable.getText().isEmpty()) {
					ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly, txtTable.getText());
					dial.center();
				}
				else {
					ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtTofill, tableOnly);
					dial.center();
				}
			}
			

		}
	};
	
	private TextBox findTxtToFill(ClickEvent event) {
		if(event.getSource() == btnColumnIdBrowse) {
			return txtColumnId;
		}
		else if(event.getSource() == btnColumnNameBrowse) {
			return txtColumnName;
		}
		else if(event.getSource() == btnColumnParentBrowse) {
			return txtParentColumn;
		}
		else if(event.getSource() == btnTableBrowse) {
			return txtTable;
		}
		else if(event.getSource() == btnColumnGeoBrowse) {
			return txtGeoColumn;
		}
		
		return null;
	}

}
