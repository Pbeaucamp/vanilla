package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class FormPanel extends Tab {
	
	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private static FormPanelUiBinder uiBinder = GWT.create(FormPanelUiBinder.class);

	interface FormPanelUiBinder extends UiBinder<Widget, FormPanel> {}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();

		String imgPlanned();
	}
	
	@UiField
	MyStyle style;

	@UiField
	SimplePanel gridPanel;

	@UiField
	Image btnAdd, btnEdit, btnClear;

	@UiField
	TextHolderBox txtSearch;

	private InfoUser infoUser;

	private DataGrid<RepositoryItem> dataGrid;
	private ListDataProvider<RepositoryItem> dataProvider;
	private SingleSelectionModel<RepositoryItem> selectionModel;
	private ListHandler<RepositoryItem> sortHandler;

	private TabManager tabManager;

	public FormPanel(InfoUser infoUser, TabManager tabManager) {
		super(tabManager, Labels.lblCnst.Form(), false);
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());
		this.infoUser = infoUser;
		
		this.tabManager = tabManager;

		DataGrid<RepositoryItem> datagrid = createFormGrid();
		gridPanel.setWidget(datagrid);

		refresh();
	}
	
	private void refresh() {
		CommonService.Connect.getInstance().getItemsByType(IRepositoryApi.FORM, -1, new GwtCallbackWrapper<List<RepositoryItem>>(this, true, true) {
			@Override
			public void onSuccess(List<RepositoryItem> result) {
				dataProvider.setList(result);
			}
		}.getAsyncCallback());
		
	}

	@UiHandler("btnAdd")
	public void onAddForm(ClickEvent event) {
		
		FormDesignPanel pan = new FormDesignPanel(infoUser, tabManager, new Form());
		
		tabManager.selectTab(pan.buildTabHeader());
		
	}

	private DataGrid<RepositoryItem> createFormGrid() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell cell = new TextCell();
		Column<RepositoryItem, String> colName = new Column<RepositoryItem, String>(cell) {

			@Override
			public String getValue(RepositoryItem object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		
		Column<RepositoryItem, String> colDesc = new Column<RepositoryItem, String>(cell) {

			@Override
			public String getValue(RepositoryItem object) {
				return object.getComment();
			}
		};
		colDesc.setSortable(true);

		Column<RepositoryItem, String> colModificationDate = new Column<RepositoryItem, String>(cell) {

			@Override
			public String getValue(RepositoryItem object) {
				return dateFormatter.format(object.getDateModification());
			}
		};
		colModificationDate.setSortable(true);

		Column<RepositoryItem, String> colModifiedBy = new Column<RepositoryItem, String>(cell) {

			@Override
			public String getValue(RepositoryItem object) {
				return "";
			}
		};
		colModifiedBy.setSortable(true);

		ButtonImageCell viewCell = new ButtonImageCell(Images.INSTANCE.ic_visibility_black_18dp(), Labels.lblCnst.ViewCurrentDocumentVersion(), style.imgGrid());
		Column<RepositoryItem, String> colView = new Column<RepositoryItem, String>(viewCell) {

			@Override
			public String getValue(RepositoryItem object) {
				return "";
			}
		};
		colView.setFieldUpdater(new FieldUpdater<RepositoryItem, String>() {

			@Override
			public void update(int index, final RepositoryItem object, String value) {
				
				CommonService.Connect.getInstance().loadForm(object, new GwtCallbackWrapper<Form>(null, false, false) {
					@Override
					public void onSuccess(Form result) {
						FormDesignPanel pan = new FormDesignPanel(infoUser, tabManager, result);
						
						tabManager.selectTab(pan.buildTabHeader());
					}
				}.getAsyncCallback());
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteDocument(), style.imgGrid());
		Column<RepositoryItem, String> colDelete = new Column<RepositoryItem, String>(deleteCell) {

			@Override
			public String getValue(RepositoryItem object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<RepositoryItem, String>() {

			@Override
			public void update(int index, final RepositoryItem object, String value) {

			}
		});

		sortHandler = new ListHandler<RepositoryItem>(new ArrayList<RepositoryItem>());
		sortHandler.setComparator(colName, new Comparator<RepositoryItem>() {

			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colDesc, new Comparator<RepositoryItem>() {

			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return o1.getComment().compareTo(o2.getComment());
			}
		});
		sortHandler.setComparator(colModificationDate, new Comparator<RepositoryItem>() {

			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return o1.getDateModification().compareTo(o2.getDateModification());
			}
		});
		sortHandler.setComparator(colModifiedBy, new Comparator<RepositoryItem>() {

			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return 0;
			}
		});

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<RepositoryItem>(1500, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colName, Labels.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "250px");
		dataGrid.addColumn(colDesc, Labels.lblCnst.Description());
		dataGrid.setColumnWidth(colDesc, "250px");
		dataGrid.addColumn(colModificationDate, Labels.lblCnst.ModificationDate());
		dataGrid.setColumnWidth(colModificationDate, "150px");
		dataGrid.addColumn(colModifiedBy, Labels.lblCnst.ModifiedBy());
		dataGrid.setColumnWidth(colModifiedBy, "150px");
		dataGrid.addColumn(colView);
		dataGrid.setColumnWidth(colView, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new ListDataProvider<RepositoryItem>();
		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<RepositoryItem>();
//		selectionModel.addSelectionChangeHandler(selectionChange);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

}
