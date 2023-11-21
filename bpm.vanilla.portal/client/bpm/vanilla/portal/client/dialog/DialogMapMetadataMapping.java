package bpm.vanilla.portal.client.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.MetadataTree;
import bpm.gwt.commons.client.tree.MetadataTree.IPropertiesListener;
import bpm.gwt.commons.client.tree.MetadataTreeItem;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.core.design.ZoneMetadataMapping;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.services.BiPortalService;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DialogMapMetadataMapping extends AbstractDialogBox implements IPropertiesListener {

	private static DialogMapMetadataMappingUiBinder uiBinder = GWT.create(DialogMapMetadataMappingUiBinder.class);

	interface DialogMapMetadataMappingUiBinder extends UiBinder<Widget, DialogMapMetadataMapping> {}

//	@UiField
//	ListBoxWithButton<String> lstZoneId;
//
//	@UiField
//	ListBoxWithButton<String> lstMetadataId;
	
	@UiField
	HTMLPanel metadataPanel, gridPanel;

	private MetadataTree metadataTree;
	private DataGrid<MapZone> datagrid;
	private ListDataProvider<MapZone> dataProvider;
	
	private Map<String, MapZone> zones;
	
	private Metadata metadata;
	private String selectedColumn;
	private List<String> columnValues;

	private MapVanilla map;

	public DialogMapMetadataMapping(MapVanilla map) {
		super("", false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.map = map;
		
//		lstMetadataId.setMultiple(true);
//		lstMetadataId.setVisibleItemCount(30);
//		lstZoneId.setMultiple(true);
//		lstZoneId.setVisibleItemCount(30);
		
//		buildDataGrid();

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		BiPortalService.Connect.getInstance().getMapZone(map, new AsyncCallback<Map<String, MapZone>>() {
			@Override
			public void onSuccess(Map<String, MapZone> result) {
				zones = result;
			}
			@Override
			public void onFailure(Throwable caught) {

			}
		});

	}
	
	private void buildDataGrid() {
		Column<MapZone, String> colZone = new Column<MapZone, String>(new TextCell()) {
			@Override
			public String getValue(MapZone object) {
				return object.getGeoId();
			}
		};
		
		Column<MapZone, String> colMetadata = new Column<MapZone, String>(new SelectionCell(columnValues)) {
			@Override
			public String getValue(MapZone object) {
				try {
					return object.getMetadataMappings().get(metadata.getItemId()).getMetadataElementId();
				} catch(Exception e) {
					return "";
				}
			}
		};
		colMetadata.setFieldUpdater(new FieldUpdater<MapZone, String>() {		
			@Override
			public void update(int index, MapZone object, String value) {
				if(object.getMetadataMappings().get(metadata.getItemId()) == null) {
					ZoneMetadataMapping mapping = new ZoneMetadataMapping();
					mapping.setDatasetId(map.getDataSetList().get(0).getId());
					mapping.setMetadataId(metadata.getItemId());
					mapping.setZoneId(object.getGeoId());
					object.getMetadataMappings().put(metadata.getItemId(), mapping);
				}
				object.getMetadataMappings().get(metadata.getItemId()).setMetadataElementId(value);
			}
		});
		
		datagrid = new DataGrid<MapZone>();
		datagrid.setSize("100%", "100%");
		
		datagrid.addColumn(colZone, "ZoneId");
		datagrid.addColumn(colMetadata, "Metadata value");
		
		dataProvider = new ListDataProvider<MapZone>();
		dataProvider.addDataDisplay(datagrid);
		
		gridPanel.clear();
		gridPanel.add(datagrid);
	}

	@UiHandler("btnMetadata")
	public void onMetadata(ClickEvent e) {
		final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.FMDT_TYPE);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					RepositoryItem item = dial.getSelectedItem();
					metadataTree = new MetadataTree(DialogMapMetadataMapping.this, true);
					metadataPanel.add(metadataTree);
					FmdtServices.Connect.getInstance().openMetadata(item, new GwtCallbackWrapper<Metadata>(null, false) {

						@Override
						public void onSuccess(Metadata result) {
							metadata = result;
							metadataTree.refresh(result);
						}
					}.getAsyncCallback());
					
				}
			}
		});
		dial.center();
	}

	@Override
	public void loadProperties(MetadataTreeItem<?> treeItem) {
		if(treeItem.getItem() instanceof DatabaseColumn) {
			selectedColumn = ((DatabaseColumn)treeItem.getItem()).getName();
			
			FmdtServices.Connect.getInstance().getTableData(metadata.getDatasource(), ((DatabaseColumn)treeItem.getItem()).getParent(), ((DatabaseColumn)treeItem.getItem()), 10000, true, new AsyncCallback<MetadataData>() {			
				@Override
				public void onSuccess(MetadataData result) {
					columnValues = new ArrayList<String>();
					for(Row r : result.getRows()) {
						columnValues.add(r.getValues().get(0));
					}
					buildDataGrid();
					refreshDatagrid();
				}
				
				@Override
				public void onFailure(Throwable caught) {

				}
			});
			

		}
	}
	
	

	private void refreshDatagrid() {
		dataProvider.setList(new ArrayList<MapZone>(zones.values()));
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DialogMapMetadataMapping.this.hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			BiPortalService.Connect.getInstance().saveMapMetadataMappings(new ArrayList<MapZone>(dataProvider.getList()), map, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					
				}

				@Override
				public void onSuccess(Void result) {
					hide();
				}
			});
		}
	};

}
