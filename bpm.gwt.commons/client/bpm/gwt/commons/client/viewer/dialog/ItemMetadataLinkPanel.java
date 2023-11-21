package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.viewer.dialog.AddMetadataLinkDialog.IRefreshProvider;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;

public class ItemMetadataLinkPanel extends Composite implements IRefreshProvider {
	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";

	private static ItemMetadataLinkPanelUiBinder uiBinder = GWT.create(ItemMetadataLinkPanelUiBinder.class);

	interface ItemMetadataLinkPanelUiBinder extends UiBinder<Widget, ItemMetadataLinkPanel> {}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	Image btnAdd;

	@UiField
	SimplePanel panelContent;
	
	private IWait waitPanel;
	private InfoUser infoUser;

	private ListDataProvider<ItemMetadataTableLink> dataProvider;

	private int itemId;

	public ItemMetadataLinkPanel(IWait waitPanel, InfoUser infoUser, int itemId) {
		this.waitPanel = waitPanel;
		this.infoUser = infoUser;
		this.itemId = itemId;

		initWidget(uiBinder.createAndBindUi(this));

		DataGrid<ItemMetadataTableLink> grid = buildGrid();
		panelContent.setWidget(grid);

		refreshItems();
	}

	public void refreshItems() {
		CommonService.Connect.getInstance().getMetadataLinks(itemId, new GwtCallbackWrapper<List<ItemMetadataTableLink>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<ItemMetadataTableLink> result) {
				loadItems(result);
			}
		}.getAsyncCallback());
	}

	private void loadItems(List<ItemMetadataTableLink> elements) {
		if(elements == null) {
			elements = new ArrayList<ItemMetadataTableLink>();
		}

		dataProvider.setList(elements);
	}

	private DataGrid<ItemMetadataTableLink> buildGrid() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell txtCell = new TextCell();
		Column<ItemMetadataTableLink, String> nameColumn = new Column<ItemMetadataTableLink, String>(txtCell) {

			@Override
			public String getValue(ItemMetadataTableLink object) {
				return object.getName();
			}
		};

		Column<ItemMetadataTableLink, String> creationDateColumn = new Column<ItemMetadataTableLink, String>(txtCell) {

			@Override
			public String getValue(ItemMetadataTableLink object) {
				if(object.getCreationDate() != null)
					return dateFormatter.format(object.getCreationDate());
				else
					return LabelsConstants.lblCnst.Unknown();
			}
		};

		Column<ItemMetadataTableLink, String> datasourceColumn = new Column<ItemMetadataTableLink, String>(txtCell) {

			@Override
			public String getValue(ItemMetadataTableLink object) {
				return object.getDatasource() != null ? object.getDatasource().getName() : LabelsConstants.lblCnst.NotDefined();
			}
		};

		Column<ItemMetadataTableLink, String> tableNameColumn = new Column<ItemMetadataTableLink, String>(txtCell) {

			@Override
			public String getValue(ItemMetadataTableLink object) {
				return object.getTableName();
			}
		};

		ButtonImageCell deleteCell = new ButtonImageCell(CommonImages.INSTANCE.ic_delete_black_18dp(), LabelsConstants.lblCnst.DeleteMetadataLink(), style.imgGrid());
		Column<ItemMetadataTableLink, String> colDelete = new Column<ItemMetadataTableLink, String>(deleteCell) {

			@Override
			public String getValue(ItemMetadataTableLink object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<ItemMetadataTableLink, String>() {

			@Override
			public void update(int index, final ItemMetadataTableLink object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeleteMetadataLink(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteItemMetadataTableLink(object);
						}
					}
				});
				dial.center();
			}
		});

		dataProvider = new ListDataProvider<ItemMetadataTableLink>(new ArrayList<ItemMetadataTableLink>());

		DataGrid.Resources resources = new CustomResources();
		DataGrid<ItemMetadataTableLink> dataGrid = new DataGrid<ItemMetadataTableLink>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(creationDateColumn, LabelsConstants.lblCnst.CreationDate());
		dataGrid.addColumn(datasourceColumn, LabelsConstants.lblCnst.DatasourceName());
		dataGrid.addColumn(tableNameColumn, LabelsConstants.lblCnst.TableName());
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoMetadataLink()));

		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		AddMetadataLinkDialog dial = new AddMetadataLinkDialog(this, infoUser.getUser(), itemId);
		dial.center();
	}

	private void deleteItemMetadataTableLink(ItemMetadataTableLink link) {
		CommonService.Connect.getInstance().deleteItemMetadataTableLink(link, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshItems();
			}
		}.getAsyncCallback());
	}

	@Override
	public void refresh() {
		refreshItems();
	}
}
