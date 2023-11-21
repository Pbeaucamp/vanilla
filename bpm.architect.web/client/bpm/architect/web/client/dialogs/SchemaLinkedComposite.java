package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
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
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.ValidationSchemaDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;

public class SchemaLinkedComposite extends Composite {

	private static SchemaLinkedCompositeUiBinder uiBinder = GWT.create(SchemaLinkedCompositeUiBinder.class);

	interface SchemaLinkedCompositeUiBinder extends UiBinder<Widget, SchemaLinkedComposite> {
	}

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

	private ListDataProvider<DocumentSchema> dataProvider;
	private Contract contract;

	public SchemaLinkedComposite(IWait waitPanel, Contract contract) {
		this.waitPanel = waitPanel;
		this.contract = contract;
		
		initWidget(uiBinder.createAndBindUi(this));

		DataGrid<DocumentSchema> grid = buildGrid();
		panelContent.setWidget(grid);

		refreshLinkedItems();
	}

	private void refreshLinkedItems() {
		CommonService.Connect.getInstance().getLinkedSchemas(contract.getId(), new GwtCallbackWrapper<List<DocumentSchema>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<DocumentSchema> result) {
				loadLinkedItems(result);
			}
		}.getAsyncCallback());
	}

	private void loadLinkedItems(List<DocumentSchema> elements) {
		if (elements == null) {
			elements = new ArrayList<>();
		}

		dataProvider.setList(elements);
	}

	private DataGrid<DocumentSchema> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<DocumentSchema, String> nameColumn = new Column<DocumentSchema, String>(txtCell) {

			@Override
			public String getValue(DocumentSchema object) {
				return object.getSchema();
			}
		};

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteLinkedItem(), style.imgGrid());
		Column<DocumentSchema, String> colDelete = new Column<DocumentSchema, String>(deleteCell) {

			@Override
			public String getValue(DocumentSchema object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<DocumentSchema, String>() {

			@Override
			public void update(int index, final DocumentSchema object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.ConfirmDeleteLinkedDocument(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteDocumentSchema(object);
						}
					}
				});
				dial.center();
			}
		});

		dataProvider = new ListDataProvider<DocumentSchema>(new ArrayList<DocumentSchema>());

		DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentSchema> dataGrid = new DataGrid<DocumentSchema>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoLinkedSchema()));

		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		final ValidationSchemaDialog dial = new ValidationSchemaDialog();
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					ClassDefinition item = dial.getSelectedSchema();
					saveDocumentSchema(item);
				}
			}
		});
	}

	private void saveDocumentSchema(ClassDefinition item) {
		DocumentSchema docItem = new DocumentSchema();
		docItem.setContractId(contract.getId());
		docItem.setSchema(item.getIdentifiant());

		ArchitectService.Connect.getInstance().saveOrUpdateSchema(docItem, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshLinkedItems();
			}
		}.getAsyncCallback());
	}

	private void deleteDocumentSchema(DocumentSchema docItem) {
		ArchitectService.Connect.getInstance().removeLinkedSchema(docItem, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshLinkedItems();
			}
		}.getAsyncCallback());
	}
}
