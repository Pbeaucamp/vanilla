package bpm.fm.designer.web.client.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.Observatory;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.panel.MainPanel;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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

public class SecurityDialog extends AbstractDialogBox {
	
	private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat("HH:mm - dd/MM/yyyy");

	private static SecurityDialogUiBinder uiBinder = GWT.create(SecurityDialogUiBinder.class);

	interface SecurityDialogUiBinder extends UiBinder<Widget, SecurityDialog> {
	}
	
	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit, imgLinkGroup, imgLinkTheme;
	
	private DataGrid<Observatory> dataGrid;
	private ListDataProvider<Observatory> dataProvider;

	private SingleSelectionModel<Observatory> selectionModel;
	
	private List<Observatory> observatories;

	public SecurityDialog() {
		super(Messages.lbl.observatoryManagement(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		createGrid();
		
		gridPanel.add(dataGrid);
		
		loadObservatories();
	}

	private void loadObservatories() {
		
		MetricService.Connection.getInstance().getObservatories(new AsyncCallback<List<Observatory>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Observatory> result) {
				Collections.sort(result, new Comparator<Observatory>() {
					@Override
					public int compare(Observatory o1, Observatory o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				observatories = result;
				refresh();
			}
		});
		
	}

	private void refresh() {
		dataProvider.setList(observatories);
	}
 	
	private void createGrid() {
		dataGrid = new DataGrid<Observatory>(15);
		dataGrid.addStyleName(style.grid());
		
		TextCell cell = new TextCell();
		
		Column<Observatory, String> colName = new Column<Observatory, String>(cell) {
			@Override
			public String getValue(Observatory object) {
				return object.getName();
			}
		};
		
		Column<Observatory, String> colDate = new Column<Observatory, String>(cell) {
			@Override
			public String getValue(Observatory object) {
				if(object.getCreationDate() != null) {
					return dateFormatter.format(object.getCreationDate());
				}
				return "";
			}
		};
		
		dataGrid.addColumn(colName, Messages.lbl.name());
		dataGrid.addColumn(colDate, Messages.lbl.CreationDate());
		
		dataGrid.setColumnWidth(colName, 100, Unit.PX);
		dataGrid.setColumnWidth(colDate, 150, Unit.PX);
		
		dataGrid.setEmptyTableWidget(new Label("No existing observatories"));
		
		dataProvider = new ListDataProvider<Observatory>();
		dataProvider.addDataDisplay(dataGrid);
	    
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
	    selectionModel = new SingleSelectionModel<Observatory>();
	    dataGrid.setSelectionModel(selectionModel);
	}

	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		ObservatoryDialog dial = new ObservatoryDialog(null);
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				loadObservatories();
			}
		});
		
		dial.center();
	}
	
	@UiHandler("imgEdit")
	public void onEdit(ClickEvent e) {
		ObservatoryDialog dial = new ObservatoryDialog(selectionModel.getSelectedObject());
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				loadObservatories();
			}
		});
		
		dial.center();
	}
	
	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		final Observatory ds = selectionModel.getSelectedObject();
		if(ds != null) {
			final InformationsDialog dial = new InformationsDialog(Messages.lbl.deleteObs(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.deleteObsPart1() + " " + ds.getName() + " " + Messages.lbl.deleteObsPart2(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						MetricService.Connection.getInstance().deleteObservatory(ds, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
								
							}
						});
						loadObservatories();
					}
				}
			});
			dial.center();
		}
	}
	
	@UiHandler("imgLinkGroup")
	public void onLink(ClickEvent e) {
		final Observatory ds = selectionModel.getSelectedObject();
		if(ds != null) {
			GroupObservatoryDialog dial = new GroupObservatoryDialog(ds);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					loadObservatories();
				}
			});
			dial.center();
		}
	}
	
	@UiHandler("imgLinkTheme")
	public void onLinkTheme(ClickEvent e) {
		final Observatory ds = selectionModel.getSelectedObject();
		if(ds != null) {
			ThemeObservatoryDialog dial = new ThemeObservatoryDialog(ds);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					loadObservatories();
				}
			});
			dial.center();
		}
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			MetricService.Connection.getInstance().updateObservatories(observatories, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Void result) {
					MainPanel.getInstance().reloadObservatories();
					SecurityDialog.this.hide();
				}
				
			});

		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			MainPanel.getInstance().reloadObservatories();
			SecurityDialog.this.hide();
		}
	};
}
