package bpm.vanilla.portal.client.panels.center;

import java.util.List;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.portal.client.dialog.ged.ArchiveTypeDialog;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ArchivePanel extends Tab {

	private static ArchivePanelUiBinder uiBinder = GWT.create(ArchivePanelUiBinder.class);

	interface ArchivePanelUiBinder extends UiBinder<Widget, ArchivePanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();

		String pager();

		String imgPlanned();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent, panelPager;

	@UiField
	Image btnEdit, btnDelete;

	private ListDataProvider<ArchiveType> dataProvider;
	private SingleSelectionModel<ArchiveType> selectionModel;
	private ListHandler<ArchiveType> sortHandler;

	public ArchivePanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.ArchiveManager(), true);
		this.add(uiBinder.createAndBindUi(this));
		
		panelContent.add(createGridData());

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		
		loadArchiveTypes();
	}
	
	private void loadArchiveTypes() {
		AdminService.Connect.getInstance().getArchiveTypes(new GwtCallbackWrapper<List<ArchiveType>>(ArchivePanel.this, true) {
			@Override
			public void onSuccess(List<ArchiveType> result) {
				dataProvider.setList(result);
			}
		}.getAsyncCallback());
	}

	private DataGrid<ArchiveType> createGridData() {
		TextCell cell = new TextCell();
		
		Column<ArchiveType, String> colName = new Column<ArchiveType, String>(cell) {
			@Override
			public String getValue(ArchiveType object) {
				return object.getName();
			}
		};
		
		Column<ArchiveType, String> colPeremption = new Column<ArchiveType, String>(cell) {
			@Override
			public String getValue(ArchiveType object) {
				return object.getPeremptionMonths() + " " + ToolsGWT.lblCnst.Months();
			}
		};
		
		Column<ArchiveType, String> colRetention = new Column<ArchiveType, String>(cell) {
			@Override
			public String getValue(ArchiveType object) {
				return object.getRetentionMonths() + " " + ToolsGWT.lblCnst.Months();
			}
		};
		
		Column<ArchiveType, String> colConservation = new Column<ArchiveType, String>(cell) {
			@Override
			public String getValue(ArchiveType object) {
				return object.getConservationMonths() + " " + ToolsGWT.lblCnst.Months();
			}
		};
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<ArchiveType> dataGrid = new DataGrid<ArchiveType>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(colName, ToolsGWT.lblCnst.Name());
		dataGrid.addColumn(colPeremption, "Peremption");
		dataGrid.addColumn(colRetention, "Retention");
		dataGrid.addColumn(colConservation, "Conservation");
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<ArchiveType>();
		dataProvider.addDataDisplay(dataGrid);
		
		selectionModel = new SingleSelectionModel<ArchiveType>();
//		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);
		
		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		final ArchiveTypeDialog dial = new ArchiveTypeDialog();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					ArchivePanel.this.showWaitPart(true);
					AdminService.Connect.getInstance().saveArchiveType(dial.getArchiveType(), new GwtCallbackWrapper<ArchiveType>(ArchivePanel.this, true) {
						@Override
						public void onSuccess(ArchiveType result) {
							loadArchiveTypes();
						}
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}
	
	@UiHandler("btnDelete")
	public void onDelete(ClickEvent event) {
		AdminService.Connect.getInstance().deleteArchiveType(selectionModel.getSelectedObject(), new GwtCallbackWrapper<Void>(ArchivePanel.this, true) {
			@Override
			public void onSuccess(Void result) {
				loadArchiveTypes();
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnEdit")
	public void onEdit(ClickEvent event) {
		final ArchiveTypeDialog dial = new ArchiveTypeDialog(selectionModel.getSelectedObject());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					ArchivePanel.this.showWaitPart(true);
					AdminService.Connect.getInstance().saveArchiveType(dial.getArchiveType(), new GwtCallbackWrapper<ArchiveType>(ArchivePanel.this, true) {
						@Override
						public void onSuccess(ArchiveType result) {
							loadArchiveTypes();
						}
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}
	
	@UiHandler("btnRefresh")
	public void onRefresh(ClickEvent event) {
		loadArchiveTypes();
	}
}
