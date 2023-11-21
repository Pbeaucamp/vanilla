package bpm.gwt.commons.client.dataset;

import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class DatasetDialog extends AbstractDialogBox {

	private static DatasetDialogUiBinder uiBinder = GWT.create(DatasetDialogUiBinder.class);
	
	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit;
	
	private DataGrid<Dataset> dataGrid;
	private ListDataProvider<Dataset> dataProvider;

	private SingleSelectionModel<Dataset> selectionModel;

	interface DatasetDialogUiBinder extends UiBinder<Widget, DatasetDialog> {
	}

	public DatasetDialog() {
		super(LabelsConstants.lblCnst.DatasetManager(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		createGrid();
		
		gridPanel.add(dataGrid);
		
		refresh();
	}
	
	private void createGrid() {
		dataGrid = new DataGrid<Dataset>(15);
		dataGrid.addStyleName(style.grid());
		
		TextCell cell = new TextCell();
		
		Column<Dataset, String> colName = new Column<Dataset, String>(cell) {
			@Override
			public String getValue(Dataset object) {
				return object.getName();
			}
		};
		
		Column<Dataset, String> colReq = new Column<Dataset, String>(cell) {
			@Override
			public String getValue(Dataset object) {
				return object.getRequest();
			}
		};
		
		Column<Dataset, String> colInfos = new Column<Dataset, String>(cell) {
			@Override
			public String getValue(Dataset object) {
				return "";
			}
		};
		
		dataGrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(colReq, LabelsConstants.lblCnst.Driver());
		dataGrid.addColumn(colInfos, LabelsConstants.lblCnst.JdbcUrl());
		
		dataGrid.setColumnWidth(colName, 125, Unit.PX);
		dataGrid.setColumnWidth(colReq, 175, Unit.PX);
		dataGrid.setColumnWidth(colInfos, 400, Unit.PX);
		
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoDatasource()));
		
		dataProvider = new ListDataProvider<Dataset>();
		dataProvider.addDataDisplay(dataGrid);
	    
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
	    selectionModel = new SingleSelectionModel<Dataset>();
	    dataGrid.setSelectionModel(selectionModel);
		
		
	}
	
	private void refresh() {
		CommonService.Connect.getInstance().getDatasets(new AsyncCallback<List<Dataset>>() {	
			@Override
			public void onSuccess(List<Dataset> result) {
				dataProvider.setList(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.GetDatasetError(), caught.getMessage(), caught);
				dial.center();
			}
		});
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DatasetDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			DatasetDialog.this.hide();
		}
	};
	
	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		DatasetWizard dial = new DatasetWizard(null); //TODO a changer avec un vrai user?
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});
		
		dial.center();
	}
	
	@UiHandler("imgEdit")
	public void onEdit(ClickEvent e) {
		DatasetWizard dial = new DatasetWizard(selectionModel.getSelectedObject(), null); //TODO a changer
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});
		
		dial.center();
	}
	
	@UiHandler("imgRefresh")
	public void onRefresh(ClickEvent e) {
		refresh();
	}
	
	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		final Dataset ds = selectionModel.getSelectedObject();
		if(ds != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteDataset(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasetPartOne() + " " + ds.getName() + " " + LabelsConstants.lblCnst.DeleteDatasetPartTwo(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteDataset(ds, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasetSuccess(), false);
								dial.center();
								
								refresh();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DeleteDatasetError(), caught.getMessage(), caught);
								dial.center();
							}
						});
					}
				}
			});
			dial.center();
		}
	}

}
