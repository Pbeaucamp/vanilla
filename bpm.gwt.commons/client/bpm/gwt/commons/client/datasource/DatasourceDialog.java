package bpm.gwt.commons.client.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dataset.DatasetDefPage;
import bpm.gwt.commons.client.dataset.DatasetWizard;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.data.Datasource;

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
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class DatasourceDialog extends AbstractDialogBox {

	private static DatasourceDialogUiBinder uiBinder = GWT.create(DatasourceDialogUiBinder.class);
	
	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit;
	
	@UiField
	SimplePanel pagerPanel;
	
	private DataGrid<Datasource> dataGrid;
	private ListDataProvider<Datasource> dataProvider;
	private ListHandler<Datasource> sortHandler;

	private SingleSelectionModel<Datasource> selectionModel;
	
	private DatasetDefPage parent = null;

	interface DatasourceDialogUiBinder extends UiBinder<Widget, DatasourceDialog> {
	}

	public DatasourceDialog() {
		super(LabelsConstants.lblCnst.DatasourceManager(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		createGrid();
		
		gridPanel.add(dataGrid);
		
		refresh();
	}
	
	public DatasourceDialog(DatasetDefPage parent) {
		super(LabelsConstants.lblCnst.DatasourceManager(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		this.parent = parent;
		createGrid();
		
		gridPanel.add(dataGrid);
		
		refresh();
	}
	
	private void createGrid() {
		dataGrid = new DataGrid<Datasource>(15);
		dataGrid.addStyleName(style.grid());
		
		TextCell cell = new TextCell();
		
		Column<Datasource, String> colName = new Column<Datasource, String>(cell) {
			@Override
			public String getValue(Datasource object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		
		Column<Datasource, String> colType = new Column<Datasource, String>(cell) {
			@Override
			public String getValue(Datasource object) {
				return object.getType().getType();
			}
		};
		colType.setSortable(true);
		
//		Column<Datasource, String> colInfos = new Column<Datasource, String>(cell) {
//			@Override
//			public String getValue(Datasource object) {
//				return "";
//			}
//		};
		
		dataGrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(colType, LabelsConstants.lblCnst.Driver());
//		dataGrid.addColumn(colInfos, LabelsConstants.lblCnst.JdbcUrl());
		
		dataGrid.setColumnWidth(colName, 325, Unit.PX);
		dataGrid.setColumnWidth(colType, 375, Unit.PX);
//		dataGrid.setColumnWidth(colInfos, 400, Unit.PX);
		
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoDatasource()));
		
		dataProvider = new ListDataProvider<Datasource>();
		dataProvider.addDataDisplay(dataGrid);
	    
		sortHandler = new ListHandler<Datasource>(new ArrayList<Datasource>());
		sortHandler.setComparator(colName, new Comparator<Datasource>() {

			@Override
			public int compare(Datasource m1, Datasource m2) {
				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});
		sortHandler.setComparator(colType, new Comparator<Datasource>() {

			@Override
			public int compare(Datasource m1, Datasource m2) {
				return m1.getType().name().compareToIgnoreCase(m2.getType().name());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);
		
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    pagerPanel.setWidget(pager);
	    
	    selectionModel = new SingleSelectionModel<Datasource>();
	    dataGrid.setSelectionModel(selectionModel);
		
		
	}
	
	private void refresh() {
		if(parent != null && parent.getWizardParent() instanceof DatasetWizard && ((DatasetWizard) parent.getWizardParent()).getUser() != null){
			CommonService.Connect.getInstance().getPermittedDatasources(new AsyncCallback<List<Datasource>>() {	
				@Override
				public void onSuccess(List<Datasource> result) {
					Collections.sort(result, new Comparator<Datasource>() {
						@Override
						public int compare(Datasource m1, Datasource m2) {
							return m1.getName().compareToIgnoreCase(m2.getName());
						}
					});
					dataProvider.setList(result);
					sortHandler.setList(dataProvider.getList());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), caught.getMessage(), LabelsConstants.lblCnst.GetDatasourceError(), caught);
					dial.center();
				}
			});
		} else {
			CommonService.Connect.getInstance().getDatasources(new AsyncCallback<List<Datasource>>() {	
				@Override
				public void onSuccess(List<Datasource> result) {
					Collections.sort(result, new Comparator<Datasource>() {
						@Override
						public int compare(Datasource m1, Datasource m2) {
							return m1.getName().compareToIgnoreCase(m2.getName());
						}
					});
					dataProvider.setList(result);
					sortHandler.setList(dataProvider.getList());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), caught.getMessage(), LabelsConstants.lblCnst.GetDatasourceError(), caught);
					dial.center();
				}
			});
		}
		
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DatasourceDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			DatasourceDialog.this.hide();
		}
	};
	
	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		DatasourceWizard dial = new DatasourceWizard();
		
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
		DatasourceWizard dial = new DatasourceWizard(selectionModel.getSelectedObject());
		
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
		final Datasource ds = selectionModel.getSelectedObject();
		if(ds != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteDatasource(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasourcePartOne() + " " + ds.getName() + " " + LabelsConstants.lblCnst.DeleteDatasourcePartTwo(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteDatasource(ds, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasourceSuccess(), false);
								dial.center();
								
								refresh();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DeleteDatasourceError(), caught.getMessage(), caught);
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
